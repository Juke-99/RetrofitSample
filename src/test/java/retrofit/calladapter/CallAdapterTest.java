package retrofit.calladapter;

import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

import retrofit.common.Utils;

public class CallAdapterTest {
	@Test
	public void parameterTypeInvalid() {
		ParameterizedType StringList = (ParameterizedType) new TypeToken<List<String>>() {}.getType();
		
		try {
			Utils.getParameterUpperBound(-1, StringList);
			fail();
		} catch(IllegalArgumentException iae) {
			assertThat(iae).hasMessage("Index -1 not in range [0,1) for java.util.List<java.lang.String>");
		}
		
		try {
			Utils.getParameterUpperBound(1, StringList);
		    fail();
		} catch (IllegalArgumentException e) {
		    assertThat(e).hasMessage("Index 1 not in range [0,1) for java.util.List<java.lang.String>");
		}
	}
}
