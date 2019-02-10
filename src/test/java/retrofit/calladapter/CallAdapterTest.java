package retrofit.calladapter;

import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
		} catch (IllegalArgumentException iae) {
		    assertThat(iae).hasMessage("Index 1 not in range [0,1) for java.util.List<java.lang.String>");
		}
	}
	
	@Test
	public void rowTypes() throws NoSuchMethodException, SecurityException {
		assertThat(Utils.getRawType(String.class)).isSameAs(String.class);
		
		Type listString = new TypeToken<List<String>>() {
			private static final long serialVersionUID = 1L;
		}.getType();
		assertThat(Utils.getRawType(listString)).isSameAs(List.class);
		
		Type arrayString = new TypeToken<String[]>() {
			private static final long serialVersionUID = 1L;
		}.getType();
		assertThat(Utils.getRawType(arrayString)).isSameAs(String[].class);
		
		Type wild = ((ParameterizedType) new TypeToken<List<? extends CharSequence>>() {
			private static final long serialVersionUID = 1L;
	    }.getType()).getActualTypeArguments()[0];
	    assertThat(Utils.getRawType(wild)).isSameAs(CharSequence.class);

	    Type wildParam = ((ParameterizedType) new TypeToken<List<? extends List<String>>>() {
			private static final long serialVersionUID = 1L;
	    }.getType()).getActualTypeArguments()[0];
	    assertThat(Utils.getRawType(wildParam)).isSameAs(List.class);

	    Type typeVar = A.class.getDeclaredMethod("method").getGenericReturnType();
	    assertThat(Utils.getRawType(typeVar)).isSameAs(Object.class);
	}
	
	@SuppressWarnings("unused")
	static private class A<T> {
		T method() {
			return null;
		}
	}
}
