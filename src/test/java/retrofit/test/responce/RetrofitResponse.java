package retrofit.test.responce;

import okhttp3.mockwebserver.MockResponse;
import retrofit.test.server.TestServer;
import retrofit.test.service.RetrofitGetService;
import retrofit2.Call;
import retrofit2.Converter.Factory;
import retrofit2.http.GET;

public class RetrofitResponse extends TestServer implements RetrofitGetService {
	
	public RetrofitResponse(Factory factory) {
		super(factory);
	}

	private RetrofitGetService service;
	
	public void testGet() {
		service = this.retrofit.create(RetrofitGetService.class);
		server.enqueue(new MockResponse());
	}

	@Override
	public Call<String> getString() {
		return null;
	}
}
