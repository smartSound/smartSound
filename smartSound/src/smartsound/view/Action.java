/*
 *	Copyright (C) 2012 André Becker
 *	
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package smartsound.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;



public class Action
{
	private final Object instance;
	private final Method method;
	private final Object defaultParams[];
	private final String description;
	private UUID uuid = UUID.randomUUID();

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(final UUID uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public Action(final Method method, final Object instance, final String description, final Object params[])
	{
		this.method = method;
		this.instance = instance;
		this.description = description;
		this.defaultParams = params;
	}

	public void execute(final Object... additionalParams)
	{
		int noOfParams = method.getGenericParameterTypes().length;
		if (additionalParams.length + this.defaultParams.length != noOfParams) {
			throw new IllegalArgumentException();
		}

		Object[] params = Arrays.copyOf(this.defaultParams, noOfParams);
		for (int i = 0; i < additionalParams.length; i++) {
			params[i + this.defaultParams.length] = additionalParams[i];
		}
		try
		{
			method.invoke(instance, params);
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch(InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Action [instance=" + instance + ", method=" + method
				+ ", params=" + Arrays.toString(defaultParams) + "]";
	}

	public boolean isParentOf(final Action other) {
		if (other == null) {
			return false;
		}
		if (!method.equals(other.method)) {
			return false;
		}

		if (defaultParams.length > other.defaultParams.length) {
			return false;
		}

		for (int i = 0; i < defaultParams.length; i++) {
			if (!defaultParams[i].equals(other.defaultParams[i])) {
				return false;
			}
		}

		return true;
	}

	public Action specialize(final String newDescription, final Object... additionalParams) {
		if (this.defaultParams.length + additionalParams.length > method.getGenericParameterTypes().length) {
			throw new IllegalArgumentException();
		}
		Object[] params = Arrays.copyOf(this.defaultParams, this.defaultParams.length + additionalParams.length);
		for (int i = 0; i < additionalParams.length; i++) {
			params[i + this.defaultParams.length] = additionalParams[i];
		}

		Action result = new Action(method, instance, newDescription, params);

		return result;
	}

	public Object getDefaultParam(final int index) {
		if (index >= defaultParams.length) {
			return null;
		}
		return defaultParams[index];
	}

	public Object getLastParam() {
		return defaultParams[defaultParams.length - 1];
	}

	public int getNoOfDefaultParams() {
		return defaultParams.length;
	}

	public Method getMethod() {
		return method;
	}

	Object[] getDefaultParameters() {
		return defaultParams;
	}
}
