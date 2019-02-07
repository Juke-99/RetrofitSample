package retrofit.test.server;

import org.junit.Before;
import org.junit.Rule;

import okhttp3.mockwebserver.MockWebServer;
import retrofit.test.factory.RetrofitGetFactory;
import retrofit2.Retrofit;

public class TestServer {
	@Rule public final MockWebServer server = new MockWebServer();
	
	public Retrofit retrofit;
	
	@Before
	public void setUp() {
		retrofit = new Retrofit.Builder()
		        .baseUrl(server.url("/"))
		        .addConverterFactory(new RetrofitGetFactory())
		        .build();
	}
}
