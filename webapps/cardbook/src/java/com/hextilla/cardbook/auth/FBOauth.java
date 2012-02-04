package com.hextilla.cardbook.auth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FBOauth implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain fc) 
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)sreq;
        HttpServletResponse res = (HttpServletResponse)sres;
        String code = sreq.getParameter("code");
        if (code != null) {
            String authURL = FBConfig.getAuthURL(code);
            URL url = new URL(authURL);
            try {
                String result = readURL(url);
                String accessToken = null;
                Integer expires = null;
                String[] pairs = result.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length != 2) {
                        throw new RuntimeException("Unexpected auth response");
                    } else {
                        if (kv[0].equals("access_token")) {
                            accessToken = kv[1];
                        }
                        if (kv[0].equals("expires")) {
                            expires = Integer.valueOf(kv[1]);
                        }
                    }
                }
                if (accessToken != null && expires != null) {
                    res.sendRedirect("http://hextilla.com/cardbook/play");
                } else {
                    throw new RuntimeException("Access token and expires not found");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

	}
	
	private String readURL(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
    }

	@Override
	public void init(FilterConfig fc) throws ServletException {
		// TODO Auto-generated method stub
	}

}
