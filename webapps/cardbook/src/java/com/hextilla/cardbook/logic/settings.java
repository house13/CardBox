package com.hextilla.cardbook.logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.hextilla.cardbook.CardbookApp;

import com.hextilla.cardbox.server.CardBoxConfig;
import com.hextilla.cardbox.server.persist.FBUserRecord;

import com.samskivert.servlet.util.ParameterUtil;
import com.samskivert.servlet.util.FriendlyException;
import com.samskivert.text.MessageUtil;
import com.samskivert.util.StringUtil;

import com.samskivert.velocity.InvocationContext;

import com.threerings.presents.server.InvocationException;

import static com.hextilla.cardbook.Log.log;

public class settings extends UserLogic 
{

	public void invoke(InvocationContext ctx, CardbookApp app, FBUserRecord user)
		throws Exception 
	{
		HttpServletRequest req = ctx.getRequest();
		ctx.put("user", user);
		ctx.put("action", "update");
		ctx.put("page", "account");
		
		String action = ParameterUtil.getParameter(req, "action", false);
        if (action.equals("update")) {
        	updateAccount(req, user);
        	app.getUserManager().updateUser(user);
        	ctx.put("status", "account.status.updated");
        }
	}
	
	protected void updateAccount (HttpServletRequest req, FBUserRecord user)
		throws Exception
	{
		// Process the updated username, restricting what's allowable
		user.username = requireString(req, "username", 15, true);
		
		// Process the boolean parameter from the checkbox
		String anon = req.getParameter("anonymous");
		user.anonymous = !StringUtil.isBlank(anon);
	}

    protected String requireString (
        HttpServletRequest req, String name, int maxLength, boolean restrict)
        throws Exception
    {
        String err = MessageUtil.compose("error.missing_field", "f." + name);
        String value = ParameterUtil.requireParameter(req, name, err);
        if (value.length() > maxLength) {
            err = MessageUtil.compose("error.field_too_long", "f." + name,
                                      MessageUtil.taint("" + maxLength));
            throw new FriendlyException(err);
        }
        if (restrict) {
            value = restrict(value);
        }
        return value;
    }
    
    protected String restrict (String name)
    	throws Exception
    {
    	Matcher m = _namePattern.matcher(name);
    	if (!m.matches())
    	{
    		String err = MessageUtil.compose("error.invalid_username", "f." + name);
    		throw new FriendlyException(err);
    	}
    	return name;
    }
    
    protected Pattern _namePattern = Pattern.compile(_nameExp);
    protected static final String _nameExp = "^[a-zA-Z0-9_-]{3,15}$";
}
