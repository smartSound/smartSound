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



public class Action
{

    public Action(Method method, Object instance, Object params[])
    {
        this.method = method;
        additionalParameter = method.getGenericParameterTypes().length > (params != null ? params.length : 0);
        this.instance = instance;
        this.params = params;
    }

    public void execute(Object param)
    {
        Object params[] = this.params;
        if(additionalParameter)
        {
            params = Arrays.copyOf(this.params, this.params.length + 1);
            params[params.length - 1] = param;
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

    private Object instance;
    private Method method;
    private Object params[];
    private boolean additionalParameter;
}
