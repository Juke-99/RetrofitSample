package retrofit.call;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import retrofit.helpers.ToStringConverterFactory;
import retrofit.test.server.TestServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CallTest extends TestServer {
	Retrofit retrofit;
	Service service;
	
	interface Service {
		@GET("/") Call<String> getString();
	    @GET("/") Call<ResponseBody> getBody();
	    @GET("/") @Streaming Call<ResponseBody> getStreamingBody();
	    @POST("/") Call<String> postString(@Body String body);
	    @POST("/{a}") Call<String> postRequestBody(@Path("a") Object a);
	}
	
	@Before
	public void setUp() {
		retrofit = toStringConverterFactorySetUp();
		service = retrofit.create(Service.class);
	}
	
	@Test
	public void syncHttp200() throws IOException {
		server.enqueue(new MockResponse().setBody("Hi"));
		
		Response<String> response = service.getString().execute();
		assertThat(response.isSuccessful()).isTrue();
	    assertThat(response.body()).isEqualTo("Hi");
	}
	
	@Test
	public void asyncHttp200() throws IOException, InterruptedException {
		server.enqueue(new MockResponse().setBody("Hi"));
		
		final AtomicReference<Response<String>> refResponse = new AtomicReference<>();
		final CountDownLatch latch = new CountDownLatch(1);
		
		service.getString().enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				refResponse.set(response);
				latch.countDown();
			}

			@Override
			public void onFailure(Call<String> call, Throwable t) {
				t.printStackTrace();
			}
		});
		
		assertTrue(latch.await(10, SECONDS));
		
		Response<String> response = refResponse.get();
		assertThat(response.isSuccessful()).isTrue();
		assertThat(response.body()).isEqualTo("Hi");
	}
	
	@Test
	public void syncHttp404() throws IOException {
		server.enqueue(new MockResponse().setResponseCode(404).setBody("Hi"));
		
		Response<String> response = service.getString().execute();
	    assertThat(response.isSuccessful()).isFalse();
	    assertThat(response.code()).isEqualTo(404);
	    assertThat(response.errorBody().string()).isEqualTo("Hi");
	}
	
	@Test
	public void transportProblem() {
		server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
		Call<String> call = service.getString();
		
		try {
			call.execute();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void conversionProblemOutgoing() throws IOException {
		Retrofit thisRetrofit = new Retrofit.Builder()
				.baseUrl(server.url("/"))
				.addConverterFactory(new ToStringConverterFactory() {
					@Override
			        public Converter<?, RequestBody> requestBodyConverter(
			        		Type type,
			        		Annotation[] parameterAnnotations, Annotation[] methodAnnotations,
			        		Retrofit retrofit) {
						return new Converter<String, RequestBody>() {
			              @Override public RequestBody convert(String value) throws IOException {
			                throw new UnsupportedOperationException("I am broken!");
			              }
			            };
			          }
				}).build();
		
		Service thisService = thisRetrofit.create(Service.class);
		Call<String> call = thisService.postString("Hi");
		
		try {
			call.execute();
			fail();
		} catch (UnsupportedOperationException e) {
		      assertThat(e).hasMessage("I am broken!");
	    }
	}
}
