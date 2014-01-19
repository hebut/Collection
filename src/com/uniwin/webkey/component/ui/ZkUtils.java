package com.uniwin.webkey.component.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

/**
 * ZkUtils 集合了zk中常用的一些功能，方便开发中的使用
 * 
 * 
 * @author sunflower
 * 
 *         时间：2010-7-9 上午09:40:26
 * 
 *         Email:zhangxuehuaemail gmail 点 com
 * 
 */
public class ZkUtils
{
	/**
     * 获取当前Execution
     * 
     * @return
     */
    public static Execution exec()
    {
        return Executions.getCurrent();
    }

    /**
     * 获得当前桌面
     * 
     * @return
     */
    public static Desktop desktop()
    {
        return exec().getDesktop();
    }

    /**
     * 返回当前桌面的webapp
     * 
     * @return
     */
    public static WebApp webApp()
    {
        return desktop().getWebApp();
    }

    /**
     * 返回指定key的webapp作用域内的对象
     * 
     * @param key
     * @return
     */
    public static Object webAppAttr(String key)
    {
        return webApp().getAttribute(key);
    }

    /**
     * 设置指定key的webapp作用域对象
     * 
     * @param key
     * @param value
     */
    public static void setAppAttr(String key, Object value)
    {
        webApp().setAttribute(key, value);
    }

    /**
     * 获得给定路径的URL
     * 
     * @param path
     * @return
     */
    public static URL getResource(String path)
    {
        return webApp().getResource(path);
    }

    /**
     * 获得跟定资源的二进制流
     * 
     * <p>
     * Notice that, since 3.6.3, this method can retreive the resource starting
     * with "~./". If the path contains the wildcard ('*'), you can use
     * {@link Execution#locate} to convert it to a proper string first.
     */
    public static InputStream getResourceAsStream(String path)
    {
        return webApp().getResourceAsStream(path);
    }

    /**
     * 获得 path虚拟路径的实际路径 . 例如, the path "/index.html" returns the absolute file
     * path on the server's filesystem would be served by a request for
     * "http://host/contextPath/index.html", where contextPath is the context
     * path of this {@link WebApp}.
     * 
     * <p>
     * Notice that ZK don't count on this method to retrieve resources. If you
     * want to change the mapping of URI to different resources, override
     * {@link org.zkoss.zk.ui.sys.UiFactory#getPageDefinition} instead.
     */
    public static String getRealPath(String path)
    {
        return webApp().getRealPath(path);
    }

    /**
     * 返回指定文件的mime类型 Returns the MIME type of the specified file, or null if the
     * MIME type is not known. The MIME type is determined by the configuration
     * of the Web container.
     * <p>
     * Common MIME types are "text/html" and "image/gif".
     */
    public static String getMimeType(String file)
    {
        return webApp().getMimeType(file);
    }

    /**
     * 获得当前请求的session
     * 
     * @return
     */
    public static Session session()
    {
        return Sessions.getCurrent();
    }

    /**
     * 设置指定key的对象到session作用域
     * 
     * @param name
     * @param value
     */
    public static void setSessionAttr(String key, Object value)
    {
        session().setAttribute(key, value);
    }

    /**
     * 获得从当前request的session中取出指定key的对象
     * 
     * @param name
     * @return
     */
    public static Object getSessionAttr(String key)
    {
        return session().getAttribute(key);
    }

    /**
     * 获得当前session作用域所有变量
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map getSessionAttrs()
    {
        return session().getAttributes();
    }

    /**
     * 获得指定名称的请求参数
     * 
     * @param name
     *            参数名
     * @return 参数值
     */
    public static Object getParameter(String name)
    {
        return exec().getParameter(name);
    }

    /**
     * 获得所有请求参数
     * 
     * @return 参数map
     */
    public static Object getParameterMap()
    {
        return exec().getParameterMap();
    }

    /**
     * 获得指定名称的请求作用域对象
     * 
     * @param name
     *            请求作用域对象名称
     * @return 作用域对象
     */
    public static Object getRequestAttr(String name)
    {
        return exec().getAttribute(name);
    }

    /**
     * 将指定key的变量设置的到request scope
     * 
     * @param name
     * @param value
     */
    public static void setRequestAttr(String name, Object value)
    {
        exec().setAttribute(name, value);
    }

    /**
     * 获得请求作用域所有对象
     * 
     * @param name
     *            请求作用域对象名称
     * @return 作用域对象
     */
    @SuppressWarnings("unchecked")
    public static Map getRequestAttrs()
    {
        return exec().getAttributes();
    }

    /**
     * 客户端重定向
     * 
     * After calling this method, the caller shall end the processing
     * immediately (by returning). All pending requests and events will be
     * dropped.
     * 
     * Parameters: uri the URI to redirect to, or null to reload the same page
     */
    public static void sendRedirect(String uri)
    {
        exec().sendRedirect(uri);
    }

    /**
     * 请求重定向
     * <p>
     * 如果页面已加载
     * 
     * @param uri
     *            定向uri
     * @param target
     *            显示uri内容的目标窗口，如果target=null，则在当前窗口显示
     */
    public static void sendRedirect(String uri, String target)
    {
        exec().sendRedirect(uri, target);
    }

    /**
     * 请求转发
     * <p>
     * 注：页面已响应客户端时， 按钮等组件监听事件不可调用该方法
     * 
     * 该方法使用范围为：客户url请求，服务器端未将响应结果响应给客户端
     * 
     * @param uri
     */
    public static void forward(String uri)
    {
        try
        {
            exec().forward(uri);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 发送一个事件到指定组件 Sends the event to the specified component and process it
     * immediately. This method can only be called when processing an event. It
     * is OK to send event to component from another page as long as they are in
     * the same desktop.
     * <p>
     * 详细中文解释见<a href="http://zh.zkoss.org/doc /devguide/ch05s03.html">这里</a>
     */
    public static void sendEvent(Component comp, Event event)
    {
        Events.sendEvent(comp, event);
    }

    /**
     * 发送一个事件到event关联的目标组件 Sends the event the target specified in the event. *
     * <p>
     * 详细中文解释见<a href="http://zh.zkoss.org/doc /devguide/ch05s03.html">这里</a>
     * <p>
     * Note: {@link Event#getTarget} cannot be null.
     */
    public static void sendEvent(Event event)
    {
        Events.sendEvent(event);
    }

    /**
     * 发送事件到当前execution Posts an event to the current execution.
     * <p>
     * The priority of the event is assumed to be 0. Refer to
     * {@link #postEvent(int, Event)}.
     * 
     * <p>
     * On the other hand, the event sent by {@link #sendEvent} is processed
     * immediately without posting it to the queue.
     * 
     * <p>
     * Note: if the target of an event is not attached to the page yet, the
     * event is ignored silently.
     * <p>
     * 详细中文解释见<a href="http://zh.zkoss.org/doc /devguide/ch05s03.html">这里</a>
     * 
     * @see #sendEvent
     * @see #echoEvent
     * @see #postEvent(int, Event)
     */
    public static final void postEvent(Event event)
    {
        exec().postEvent(event);
    }

    /**
     * 发送一个命名的事件到指定target Posts an instance of {@link Event} to the current
     * execution.
     * <p>
     * The priority of the event is assumed to be 0. Refer to
     * {@link #postEvent(int, String, Component, Object)}. 详细中文解释见<a
     * href="http://zh.zkoss.org /doc/devguide/ch05s03.html">这里</a>
     * 
     * @see #postEvent(Event)
     * @see #postEvent(int, String, Component, Object)
     */
    public static final void postEvent(String name, Component target,
            Object data)
    {
        postEvent(0, name, target, data);
    }

    /**
     * Posts an event to the current execution with the specified priority.
     * 
     * <p>
     * The posted events are processed from the higher priority to the lower
     * one. If two events are posted with the same priority, the earlier the
     * event being posted is processed earlier (first-in-first-out).
     * 
     * <p>
     * The priority posted by posted by {@link #postEvent(Event)} is 0.
     * Applications shall not use the priority higher than 10,000 and lower than
     * -10,000 since they are reserved for component development.
     * 
     * @param priority
     *            the priority of the event.
     * @since 3.0.7
     */
    public static final void postEvent(int priority, Event event)
    {
        exec().postEvent(priority, event);
    }

    /**
     * Posts an instance of {@link Event} to the current execution with the
     * specified priority.
     * 
     * <p>
     * The posted events are processed from the higher priority to the lower
     * one. If two events are posted with the same priority, the earlier the
     * event being posted is processed earlier (first-in-first-out).
     * 
     * <p>
     * The priority posted by posted by {@link #postEvent(Event)} is 0.
     * Applications shall not use the priority higher than 10,000 and lower than
     * -10,000 since they are reserved for component development.
     * 
     * @param priority
     *            the priority of the event.
     * @since 3.0.7
     */
    public static final void postEvent(int priority, String name,
            Component target, Object data)
    {
        Events.postEvent(priority, name, target, data);
    }

    /**
     * 
     * 回显事件. By echo we mean the event is fired after the client receives the AU
     * responses and then echoes back. In others, the event won't be execute in
     * the current execution. Rather, it executes after the client receives the
     * AU responses and then echoes back the event back.
     * 
     * <p>
     * It is usually if you want to prompt the user before doing a long
     * operartion. A typical case is to open a hightlighted window to prevent
     * the user from clicking any button before the operation gets done.
     * 
     * @since 3.0.2
     * @see #sendEvent
     * @see #echoEvent
     * @param name
     *            the event name, such as onSomething
     * @param target
     *            the component to receive the event (never null).
     * @param data
     *            the extra information, or null if not available. It will
     *            become {@link Event#getData}.
     */
    public static final void echoEvent(String name, Component target,
            String data)
    {
        Events.echoEvent(name, target, data);
    }

    /**
     * 消息提示框
     * <p>
     * <b style="color:red;"/>注意</b>：自从 zk5.0以后，默认禁用本地事件进程,见zk.xml配置文件
     * &lt;disable-event- thread&gt;true&lt;/disable-event-thread&gt;启用事件处理线程，
     * 请将true改为false ,如果此行注释掉或者没有此行，请添加配置&lt;disable-event-
     * thread&gt;false&lt;/disable -event-thread
     * &gt;当禁用事件线程时，如果您使用了messagebox的返回值作为判断的话，那么if语句内的代码永远都不会执行。这两个的区别的详细说明见 <a
     * href="http://sunflowers.javaeye.com/blog/686243"& gt;这里<a>
     * 
     * @param message
     *            消息内容
     * @param title
     *            窗口标题
     */
    public static void showInformationbox(String message, String title)
    {
        show(message, title, Messagebox.INFORMATION);
    }

    /**
     * 询问提示框
     * <p>
     * <b style="color:red;"/>注意</b>：自从 zk5.0以后，默认禁用本地事件进程,见zk.xml配置文件
     * &lt;disable-event- thread&gt;true&lt;/disable-event-thread&gt;启用事件处理线程，
     * 请将true改为false ,如果此行注释掉或者没有此行，请添加配置&lt;disable-event-
     * thread&gt;false&lt;/disable -event-thread
     * &gt;当禁用事件线程时，如果您使用了messagebox的返回值作为判断的话，那么if语句内的代码永远都不会执行。这两个的区别的详细说明见 <a
     * href="http://sunflowers.javaeye.com/blog/686243"& gt;这里<a>
     * 
     * @param message
     *            提示内容
     * @param title
     *            窗口标题
     * @return boolean 类型，true确认，false否
     */
    public static boolean showQuestionbox(String message, String title)
    {
        try
        {
            int flag = Messagebox
                    .show(message, title, Messagebox.OK | Messagebox.CANCEL,
                            Messagebox.QUESTION, Messagebox.CANCEL);
            return flag == Messagebox.OK;
        } catch (InterruptedException e)
        {
        }
        return false;
    }

    /**
     * 警告提示框
     * <p>
     * <b style="color:red;"/>注意</b>：自从 zk5.0以后，默认禁用本地事件进程,见zk.xml配置文件
     * &lt;disable-event- thread&gt;true&lt;/disable-event-thread&gt;启用事件处理线程，
     * 请将true改为false ,如果此行注释掉或者没有此行，请添加配置&lt;disable-event-
     * thread&gt;false&lt;/disable -event-thread
     * &gt;当禁用事件线程时，如果您使用了messagebox的返回值作为判断的话，那么if语句内的代码永远都不会执行。这两个的区别的详细说明见 <a
     * href="http://sunflowers.javaeye.com/blog/686243"& gt;这里<a>
     * 
     * @param message
     *            警告内容
     * @param title
     *            窗口标题
     */
    public static void showExclamationbox(String message, String title)
    {
        show(message, title, Messagebox.EXCLAMATION);
    }

    /**
     * 错误提示框
     * <p>
     * <b style="color:red;"/>注意</b>：自从 zk5.0以后，默认禁用本地事件进程,见zk.xml配置文件
     * &lt;disable-event- thread&gt;true&lt;/disable-event-thread&gt;启用事件处理线程，
     * 请将true改为false ,如果此行注释掉或者没有此行，请添加配置&lt;disable-event-
     * thread&gt;false&lt;/disable -event-thread
     * &gt;当禁用事件线程时，如果您使用了messagebox的返回值作为判断的话，那么if语句内的代码永远都不会执行。这两个的区别的详细说明见 <a
     * href="http://sunflowers.javaeye.com/blog/686243"& gt;这里<a>
     * 
     * @param message
     *            提示内容
     * @param title
     *            窗口标题
     */
    public static void showErrorbox(String message, String title)
    {
        show(message, title, Messagebox.ERROR);
    }

    /**
     * 显示Information提示框
     * 
     * @param message
     *            提示内容
     * @param title
     *            窗口标题
     * @param icon
     *            窗口图标：Messagebox.INFORMATION，Messagebox.QUESTION，Messagebox.EXCLAMATION
     *            ，Messagebox.ERROR
     */
    private static void show(String message, String title, String icon)
    {
        try
        {

            Messagebox.show(message, title, Messagebox.OK, icon);
        } catch (InterruptedException e)
        {
            // do nothing
        }
    }

    /**
     * 返回远端完全限定名，或者最后一次发送请求的代理
     */
    public static String getRemoteHost()
    {
        return exec().getRemoteHost();
    }

    /**
     * 
     * 返回请求客户端的IP地址
     */
    public static String getRemoteAddr()
    {
        return exec().getRemoteAddr();
    }

    /**
     * 返回本地请求对象（即ServletRequest）,如果不可用返回null
     * 
     * <p>
     * The returned object depends on the Web container. If it is based Java
     * servlet container, an instance of javax.servlet.ServletRequest is
     * returned.
     */
    public static Object getNativeRequest()
    {
        return exec().getNativeRequest();
    }

    /**
     * 返回本地响应对象（即ServletResponse）,如果不可用返回null
     * 
     * <p>
     * The returned object depends on the Web container. If it is based Java
     * servlet container, an instance of javax.servlet.ServletResponse is
     * returned.
     */
    public static Object getNativeResponse()
    {
        return exec().getNativeResponse();
    }

    /**
     * 开始处理
     * <p>
     * 在浏览器左上角，会出现一个"处理中，请稍后..."提示框<br />
     * 并禁用桌面上所有组件的行为，用户无法操作
     * 
     * @since 5.0
     */
    public static void startProcessing()
    {
        Clients.evalJavaScript("zk.startProcessing(1);");
    }

    /**
     * 结束处理
     * <p>
     * 隐藏浏览器左上角的"处理中，请稍后..."提示框 ，<br />
     * 并启用用桌面上所有组件的行为，允许用户操作
     * 
     * @since 5.0
     */
    public static void endProcessing()
    {
        Clients.evalJavaScript("zk.endProcessing();");
    }

    /**
     * 使用另外一个组件替换组件
     * 
     * @param oldc
     *            删除的组件
     * @param newc
     *            添加的组件
     * @exception IllegalArgumentException
     *                如果旧组件无父组件或无所属page
     * @since 3.5.2
     */
    public static void replaceComponent(Component oldc, Component newc)
    {
        Components.replace(oldc, newc);
    }

    /**
     * 替换指定组件的所有子组件. 其与下面代码是等效的
     * 
     * <pre>
     * &lt;code&gt;parent.getChildren().clear(); 
     * parent.getChildren().addAll(newChildren); 
     * &lt;/code&gt;
     * </pre>
     * 
     * @since 3.5.2
     */
    @SuppressWarnings("unchecked")
    public static void replaceChildren(Component parent, Collection newChildren)
    {
        Components.replaceChildren(parent, newChildren);
    }

    /**
     * 检测node1是否是node2的祖先，如果相同返回true
     */
    public static boolean isAncestor(Component node1, Component node2)
    {
        return Components.isAncestor(node1, node2);// �÷���û��null�ж�
    }

    /**
     * 删除指定组件的所有子组件 ，它与代码 <code>comp.getChildren().clear()</code>.是等效的
     */
    public static void removeAllChildren(Component comp)
    {
        if (comp == null || comp.getChildren().size() < 1)
            return;
        comp.getChildren().clear();
    }

    /**
     * 返回组件是否真正可见的 (所有父组件均可见).
     * <p>
     * 注意: 如果comp为空，返回true.另外, 它不能检测根节点是否可见,例如
     * <code>Components.isRealVisible(getParent())</code>.
     * 
     * @see Component#isVisible
     */
    public static boolean isRealVisible(Component comp)
    {
        return Components.isRealVisible(comp);
    }

    /**
     * 获得真正原始事件
     * 
     * @param event
     * @return
     */
    public static Event getRealOrigin(ForwardEvent event)
    {
        return Events.getRealOrigin(event);
    }
}