/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2015 vanita5 <mail@vanita5.de>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.vanita5.twittnuker.util.net;

import android.net.Uri;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import de.vanita5.twittnuker.TwittnukerConstants;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import twitter4j.TwitterException;
import twitter4j.auth.Authorization;
import twitter4j.http.HostAddressResolver;
import twitter4j.http.HttpClient;
import twitter4j.http.HttpClientConfiguration;
import twitter4j.http.HttpParameter;
import twitter4j.http.HttpRequest;
import twitter4j.http.HttpResponse;
import twitter4j.http.RequestMethod;

public class OkHttpClientImpl implements HttpClient, TwittnukerConstants {

    public static final MediaType APPLICATION_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
	private final HttpClientConfiguration conf;
	private final OkHttpClient client;
    private final HostAddressResolver resolver;

	public OkHttpClientImpl(HttpClientConfiguration conf) {
		this.conf = conf;
        this.resolver = conf.getHostAddressResolverFactory().getInstance(conf);
		this.client = createHttpClient(conf);
	}

	@Override
	public HttpResponse request(HttpRequest req) throws TwitterException {
		final Builder builder = new Builder();
		for (Entry<String, String> headerEntry : req.getRequestHeaders().entrySet()) {
			builder.header(headerEntry.getKey(), headerEntry.getValue());
		}
		final Authorization authorization = req.getAuthorization();
		if (authorization != null) {
			final String authHeader = authorization.getAuthorizationHeader(req);
			if (authHeader != null) {
				builder.header("Authorization", authHeader);
			}
		}
		try {
            setupRequestBuilder(builder, req);
            final Response response = client.newCall(builder.build()).execute();
            return new OkHttpResponse(conf, null, response);
        } catch (IOException e) {
            throw new TwitterException(e);
        }
    }

    @Override
    public void shutdown() {

    }

    private OkHttpClient createHttpClient(HttpClientConfiguration conf) {
        final OkHttpClient client = new OkHttpClient();
        final boolean ignoreSSLError = conf.isSSLErrorIgnored();
        client.setHostnameVerifier(new HostResolvedHostnameVerifier(ignoreSSLError));
        client.setSslSocketFactory(new HostResolvedSSLSocketFactory(resolver, ignoreSSLError));
        client.setSocketFactory(new HostResolvedSocketFactory(resolver));
        if (conf.isProxyConfigured()) {
            client.setProxy(new Proxy(Type.HTTP, InetSocketAddress.createUnresolved(conf.getHttpProxyHost(),
                    conf.getHttpProxyPort())));
        }
        return client;
    }

    private RequestBody getRequestBody(HttpParameter[] params) throws IOException {
        if (params == null) return null;
        if (!HttpParameter.containsFile(params)) {
            return RequestBody.create(APPLICATION_FORM_URLENCODED, HttpParameter.encodeParameters(params));
        }
        if (params.length == 1) {
            final HttpParameter param = params[0];
            if (param.hasFileBody()) {
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                return RequestBody.create(MediaType.parse(param.getContentType()), os.toByteArray());
            } else {
                return RequestBody.create(MediaType.parse(param.getContentType()), param.getFile());
            }
        }
        String boundary = String.format("----%s", UUID.randomUUID().toString());
        final MediaType mediaType = MediaType.parse("multipart/form-data; boundary=" + boundary);
        boundary = "--" + boundary;
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (final HttpParameter param : params) {
            os.write(String.format("%s\r\n", boundary).getBytes("UTF-8"));
            if (param.isFile()) {
                os.write(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n", param.getName(), param.getFileName()).getBytes("UTF-8"));
                os.write(String.format("Content-Type: %s\r\n\r\n", param.getContentType()).getBytes("UTF-8"));
                final BufferedInputStream in = new BufferedInputStream(param.hasFileBody() ?
                        param.getFileBody() : new FileInputStream(param.getFile()));
                byte[] buff = new byte[8192];
                while (in.read(buff) != -1) {
                    os.write(buff);
                }
                in.close();
            } else {
                os.write(String.format("Content-Disposition: form-data; name=\"%s\"\r\n", param.getName()).getBytes("UTF-8"));
                os.write("Content-Type: text/plain; charset=UTF-8\r\n\r\n".getBytes("UTF-8"));
                os.write(param.getValue().getBytes("UTF-8"));
            }
            os.write("\r\n".getBytes("UTF-8"));
        }
        os.write(String.format("%s--\r\n", boundary).getBytes("UTF-8"));
        return RequestBody.create(mediaType, os.toByteArray());
    }

    private void setupRequestBuilder(Builder builder, HttpRequest req) throws IOException {
        final Uri.Builder uriBuilder = Uri.parse(req.getURL()).buildUpon();
        final RequestMethod method = req.getMethod();
        if (method != RequestMethod.POST && method != RequestMethod.PUT) {
            final HttpParameter[] parameters = req.getParameters();
            if (parameters != null) {
                for (HttpParameter param : parameters) {
                    uriBuilder.appendQueryParameter(param.getName(), param.getValue());
                }
            }
        }
        final Uri uri = uriBuilder.build();
			switch (req.getMethod()) {
				case GET: {
					builder.get();
					break;
				}
				case POST: {
					builder.post(getRequestBody(req.getParameters()));
					break;
				}
				case DELETE: {
					builder.delete();
					break;
				}
				case HEAD: {
					builder.head();
					break;
				}
				case PUT: {
					builder.put(getRequestBody(req.getParameters()));
					break;
				}
				default: {
					throw new AssertionError();
				}
			}
        builder.url(uri.toString());
	}

	private static class OkHttpResponse extends HttpResponse {

		private final Response response;

		public OkHttpResponse(HttpClientConfiguration conf, HttpRequest request, Response response)
				throws TwitterException, IOException {
			super(conf);
			this.response = response;
			statusCode = response.code();
			if ("gzip".equals(response.header("Content-Encoding"))) {
				is = new GZIPInputStream(response.body().byteStream());
			} else {
				is = response.body().byteStream();
			}
			if (!response.isSuccessful()) {
				throw new TwitterException(response.message(), request, this);
			}
		}

		@Override
		public void disconnect() throws IOException {
			if (is != null) {
				is.close();
			}
		}

		@Override
		public String getResponseHeader(String name) {
			return response.header(name);
		}

		@Override
		public Map<String, List<String>> getResponseHeaderFields() {
			final Headers headers = response.headers();
			final Map<String, List<String>> maps = new HashMap<>();
			for (final String name : headers.names()) {
				final List<String> values = new ArrayList<>(1);
				for (final String value : headers.values(name)) {
					values.add(value);
				}
				maps.put(name, values);
			}
			return maps;
		}
	}
}