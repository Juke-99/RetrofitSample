package retrofit.call;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import retrofit.test.server.TestServer;
import retrofit2.Call;
import retrofit2.Callback;
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

public class CallTest extends TestServer {
	interface Service {
		@GET("/") Call<String> getString();
	    @GET("/") Call<ResponseBody> getBody();
	    @GET("/") @Streaming Call<ResponseBody> getStreamingBody();
	    @POST("/") Call<String> postString(@Body String body);
	    @POST("/{a}") Call<String> postRequestBody(@Path("a") Object a);
	}
	
	@Test
	public void syncHttp200() throws IOException {
		Retrofit retrofit = toStringConverterFactorySetUp();
		Service service = retrofit.create(Service.class);
		
		server.enqueue(new MockResponse().setBody("Hi"));
		
		Response<String> response = service.getString().execute();
		assertThat(response.isSuccessful()).isTrue();
	    assertThat(response.body()).isEqualTo("Hi");
	}
	
	@Test
	public void asyncHttp200() throws IOException, InterruptedException {
		Retrofit retrofit = toStringConverterFactorySetUp();
		Service service = retrofit.create(Service.class);
		
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
}
