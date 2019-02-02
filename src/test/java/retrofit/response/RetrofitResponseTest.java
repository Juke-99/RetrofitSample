package retrofit.response;

import org.junit.Test;

import okhttp3.mockwebserver.MockResponse;
import retrofit.test.server.TestServer;
import retrofit2.Call;
import retrofit2.http.GET;

public class RetrofitResponseTest extends TestServer {

	private RetrofitGetService service;
	
	@Test
	public void testGet() {
		service = retrofit.create(RetrofitGetService.class);
		server.enqueue(new MockResponse());
	}

	interface RetrofitGetService {
		@GET("/") Call<String> getString();
	}
}
