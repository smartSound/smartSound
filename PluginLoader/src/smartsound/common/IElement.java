package smartsound.common;

import java.util.UUID;

public interface IElement {
	public UUID getUUID();
	public void act(String... actionType);
	public IElement add(String elementType, Object... params);
	public void remove();
	public NameValuePair[] get(String... propertyName);
	public void set(NameValuePair... params);

	public class NameValuePair {
		public String name;
		public Object value;

		private NameValuePair() {}

		public static NameValuePair create(final String name, final Object value) {
			NameValuePair result = new NameValuePair();
			result.name = name;
			result.value = value;
			return result;
		}
	}
}
