package retrofit.test.server;

import org.junit.Before;
import org.junit.Rule;

import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class TestServer {
	@Rule public final MockWebServer server = new MockWebServer();
	
	private Converter.Factory factory;
	public Retrofit retrofit;
	
	public TestServer(Converter.Factory factory) {
		this.factory = factory;
	}
	
	@Before
	public void setUp() {
		retrofit = new Retrofit.Builder()
		        .baseUrl(server.url("/"))
		        .addConverterFactory(this.factory)
		        .build();
	}
}
