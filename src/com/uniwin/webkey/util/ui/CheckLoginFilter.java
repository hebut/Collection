package com.uniwin.webkey.util.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.uniwin.webkey.core.ui.LoginWin;

/**
 * 用于检测用户是否登陆的过滤器，如果未登录，则重定向到指的登录页面
 * 
 * 
 * 配置参数
 * 
 * 
 * checkSessionKey 需检查的在 Session 中保存的关键字
 * 
 * redirectURL 如果用户未登录，则重定向到指定的页面，URL不包括 ContextPath
 * 
 * notCheckURLList 不做检查的URL列表，以分号分开，并且 URL 中不包括 ContextPath
 */
public class CheckLoginFilter implements Filter
{
    protected FilterConfig       filterConfig    = null;

    private String               redirectURL     = null;

    private List                 notCheckURLList = new ArrayList();

    private String               sessionKey      = null;

    public static Map            sessionsMap     = new HashMap();

    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        if (session != null && !session.equals("") && session.getId() != null
                && !session.getId().equals("")
                && !(session.getAttribute("rbac_user") == null))
        {
            sessionsMap.put(session.getId(), session);
        }
        session.getServletContext();
        if (sessionKey == null)
        {
            filterChain.doFilter(request, response);
            return;
        }
        if ((!checkRequestURIIntNotFilterList(request))
                && session.getAttribute(sessionKey) == null)
        {
            response.sendRedirect(request.getContextPath() + redirectURL);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy()
    {
        notCheckURLList.clear();
    }

    private boolean checkRequestURIIntNotFilterList(HttpServletRequest request)
    {
        String uri = request.getServletPath()
                + (request.getPathInfo() == null ? "" : request.getPathInfo());
        boolean match = notCheckURLList.contains(uri);
        for (int i = 0; !match && (i < notCheckURLList.size()); i++)
        {
            match = uri.matches((String) notCheckURLList.get(i));
        }
        return match;
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
        this.filterConfig = filterConfig;
        redirectURL = filterConfig.getInitParameter("redirectURL");
        sessionKey = filterConfig.getInitParameter("checkSessionKey");
        String notCheckURLListStr = filterConfig
                .getInitParameter("notCheckURLList");
        if (notCheckURLListStr != null)
        {
            StringTokenizer st = new StringTokenizer(notCheckURLListStr, ";");
            notCheckURLList.clear();
            while (st.hasMoreTokens())
            {
                notCheckURLList.add(st.nextToken());
            }
        }
    }

    public static void reloadSessionsData()
    {
        Iterator iter = CheckLoginFilter.sessionsMap.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            HttpSession currentSession = (HttpSession) val;
            if (currentSession != null)
            {
                new LoginWin().initFrameworkSessionDataByUserId(currentSession);
            }
        }
    }
}