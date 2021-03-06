package retrofit.test.server;

import org.junit.Rule;

import okhttp3.mockwebserver.MockWebServer;
import retrofit.helpers.ToStringConverterFactory;
import retrofit2.Retrofit;

public class TestServer {
	@Rule public final MockWebServer server = new MockWebServer();
	
	public Retrofit retrofit;
	
	public Retrofit toStringConverterFactorySetUp() {
		return new Retrofit.Builder()
				.baseUrl(server.url("/"))
				.addConverterFactory(new ToStringConverterFactory())
				.build();
	}
}
