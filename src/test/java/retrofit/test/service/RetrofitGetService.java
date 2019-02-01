package retrofit.test.service;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitGetService {
	@GET("/") Call<String> getString();
}
