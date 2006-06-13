/*
 * @(#)TrayIconManager.java
 *
 * Copyright (c) 2001 Jangho Hwang,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	3. Neither the name of the JangHo Hwang nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *    $Id: TrayIconManager.java,v 1.1 2006/06/13 03:11:10 baojie Exp $
 */
package other.rath.tools.tray;

import java.util.HashMap;
import java.util.Iterator;

import java.awt.Frame;
import java.awt.Point;

import other.rath.tools.Win32Toolkit;

/**
 * 트레이 아이콘들을 관리해주는 클래스이다.
 * 이 클래스는 복수의 인스턴스 생성을 허용하지 않는다.
 * <p>
 * 또한 트레이 아이콘은 비정상 종료하였을때, 시스템 트레이에 아이콘이 그대로
 * 남아있는 경우가 많다. 이것을 방지하기 위해 Runtime.addShutdownHook 함수를 사용한다.
 * 이것을 사용하기 때문에, JDK 1.3 이상을 필요로 한다.
 * <p>
 * 대신 Platform은 Windows 95/98/ME와 NT 4.0/2000 이상을 모두 지원한다.
 * <br>단, 풍선도움말 기능을 이용하기 위해 AdvancedTrayIcon을 사용하려 한다면,
 * Windows ME/2000 이상의 플랫폼이여야만 한다. (Shell32.dll version 5.0이 필요하다.
 * ME나 2000에는 기본적으로 5.0 버젼의 Shell32.dll 이 포함되어 있다)
 * <p>
 * 아래의 예제를 보면 쉽게 사용방법을 익힐 수 있을 것이다.
 * <p>
 * <pre><code>
 * Win32Toolkit toolkit = Win32Toolkit.getInstance();
 * ...
 * TrayIconManager tray = new TrayIconManager( tookit );
 *
 * <font color=green><i>// 풍선도움말을 가지는 트레이아이콘 객체를 생성한다.</font></i>
 * AdvancedTrayIcon icon = new AdvancedTrayIcon(
 *     new ImageIcon("cute.gif").getImage(), "이쁜트레이" );
 *
 * <font color=green><i>// 풍선도움말의 제목을 설정한다.</font></i>
 * icon.setBaloonTitle( "아희 래쓰 멋쟁이" );
 * <font color=green><i>// 풍선도움말 내용을 정한다. 개행문자(\n)이 적용된다.</font></i>
 * icon.setBaloonText( "래쓰님이 등장 하였습니다.\n모두 자리에서 일어나주세요" );
 * <font color=green><i>// 풍선도움말 제목 옆에 표시할 아이콘을 설정한다.</font></i>
 * icon.setBaloonIcon( icon.ICON_INFORMATION );
 *
 * <font color=green><i>// 이벤트리스너와 함께, 시스템 트레이에 실제로 등록한다.</font></i>
 * tray.addTrayIcon( icon, new TrayEventAdapter() {
 *     public void mouseDblClicked( Point p )
 *     {
 *         System.out.println( p + " 좌표에서 더블클릭했다." );
 *     }
 * });
 *
 * <font color=green><i>// 중간 변경 기능을 테스트 하기 위해 10초만 쉰다.</font></i>
 * Thread.currentThread().sleep( 10000L );
 *
 * <font color=green><i>// 아이콘 정보를 수정한다.</font></i>
 * icon.setIcon( new ImageIcon("angry.gif").getImage() );
 * icon.setBaloonTitle( "멋쟁이 좋아하구 있네" );
 * icon.setBaloonIcon( icon.ICON_ERROR );
 *
 * <font color=green><i>// 변경된 필드정보와 함께 실제로 변경내용을 적용시킨다.</font></i>
 * tray.modifyTrayIcon( info, TrayIcon.TYPE_ICON | TypeIcon.TYPE_BALOON );
 *
 * </code></pre>
 *
 * Windows 95/98 및 NT 4.0이상 사용자의 경우 풍선도움말만 사용하지 못할뿐 일반
 * 기능은 모두 사용할 수 있다. 단, {@link AdvancedTrayIcon AdvancedTrayIcon}이 아닌
 * {@link TrayIcon TrayIcon} 클래스를 사용해야 할 것이다.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: TrayIconManager.java,v 1.1 2006/06/13 03:11:10 baojie Exp $, since 2001/12/17
 */
public class TrayIconManager
{
    private final Win32Toolkit toolkit;
    private int uniqueId = 0;
    private long handle = 0L;
    private HashMap listenerMap = null;

    /**
     * Windows의 시스템 트레이를 사용할 수 있게 해주는 TrayIconManager 인스턴스를
     * 생성한다. Windows 고유 기능이기 때문에, Win32Toolkit 인스턴스를 넘겨주어야 한다.
     */
    public TrayIconManager(Win32Toolkit toolkit)
    {
        this.toolkit = toolkit;
        this.listenerMap = new HashMap();
    }

    /**
     * 시스템트레이영역에 새로운 트레이 아이콘을 등록한다.
     *
     * @param  info     새로운 트레이 아이콘의 정보를 가진 객체
     * @param  listener 등록된 트레이 영역에 대한 이벤트를 청취할 이벤트리스너
     */
    public synchronized void addTrayIcon(TrayIcon info,
                                         TrayEventListener listener)
    {
        if (listenerMap.size() == 0)
        {
            final Frame temp = new Frame("");
            new Thread(new Runnable()
            {
                public void run()
                {
                    temp.pack();
                    handle = createTrayHandle(temp);
                    temp.dispose();
                }
            }).start();

            try
            {
                int retry = 0;
                while (handle == 0 || retry < 20)
                {
                    Thread.currentThread().sleep(50L);
                    retry++;
                }
            }
            catch (InterruptedException e)
            {}
            if (handle == 0)
            {
                throw new IllegalStateException("handle is not valid");
            }

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                public void run()
                {
                    if (listenerMap.size() == 0)
                    {
                        return;
                    }

                    for (Iterator i = listenerMap.keySet().iterator();
                         i.hasNext(); )
                    {
                        String id = (String) i.next();
                        removeTrayIcon0(handle, Integer.parseInt(id));
                    }
                }
            });
        }
        if (handle == 0)
        {
            throw new IllegalStateException("handle is not valid");
        }

        info.setUniqueID(uniqueId++);

        NativeIcon nicon = info.getIcon();
        if (!nicon.isHandleCreated())
        {
            nicon.setIconHandle(toolkit.createIconFromImage(nicon.getImage()));
        }

        listenerMap.put(String.valueOf(info.getUniqueID()), listener);

        addTrayIcon0(handle, info);
    }

    /**
     * 기존에 등록되어있던 트레이아이콘의 속성을 변경한다.
     * <p>
     * 주의: TrayIconInfo 객체를 새로 생성하면 안되며, 반드시 addTrayIcon에 집어넣었던
     * 객체에서 속성만 변경하여야 한다. 그렇지 않으면 IllegalArgumentException을 던질 것이다.
     *
     * @param  info        변경된 정보를 가지고 있는 기존 TrayIconInfo 객체
     * @param  modifyField TrayIconInfo.ICON, TrayIconInfo.TIP 등 실제로 값이 변한
     *                     필드들의 논리 합. (ex: <b>TrayIconInfo.ICON | TrayIconInfo.TIP</b> )
     */
    public synchronized void modifyTrayIcon(TrayIcon info, int modifyField)
    {
        long iconHandle = 0L;
        String tipMessage = null;

        if (!listenerMap.containsKey(String.valueOf(info.getUniqueID())))
        {
            throw new IllegalArgumentException("unregistered trayicon");
        }
        if (handle == 0)
        {
            throw new IllegalStateException("handle is not valid");
        }

        if ( (modifyField & TrayIcon.TYPE_ICON) == TrayIcon.TYPE_ICON)
        {
            NativeIcon nicon = info.getIcon();
            if (!nicon.isHandleCreated())
            {
                iconHandle = toolkit.createIconFromImage(nicon.getImage());
                nicon.setIconHandle(iconHandle);
            }
            else
            {
                iconHandle = nicon.getIconHandle();
            }
        }

        setTrayIcon0(handle, info, modifyField);
    }

    /**
     * 해당 트레이 아이콘을 시스템 트레이에서 해제한다.
     * 만약 등록하지 않은 TrayIconInfo 객체를 해제하려 한다면,
     * java.lang.IllegalArgumentException을 던질 것이다.
     */
    public synchronized void removeTrayIcon(TrayIcon info)
    {
        // getUniqueID와 HWND로 Shell_NotifyIcon( NIM_REMOVE )를 수행하도록 한다.
        int uid = info.getUniqueID();
        if (listenerMap.remove(String.valueOf(uid)) == null)
        {
            throw new IllegalArgumentException("unregistered trayicon");
        }
        if (handle == 0)
        {
            throw new IllegalStateException("handle is not valid");
        }

        removeTrayIcon0(handle, uid);
        toolkit.destroyIcon(info.getIconHandle());

        if (listenerMap.size() == 0)
        {
            destroyTrayHandle(handle);
        }
    }

    /**
     * 특정 NativeIcon이 더이상 필요하지 않을때, Java image와 os specify 하게
     * 생성된 아이콘 핸들을 모두 해제해준다.
     */
    public void removeNativeIcon(NativeIcon icon)
    {
        long iconHandle = icon.getIconHandle();
        if (iconHandle != 0)
        {
            toolkit.destroyIcon(iconHandle);
        }

        icon.flush();
    }

    /**
     * native WindowProc 으로부터 호출되는 이벤트 발송 메소드이다.
     */
    private void fireTrayEvent(int uid, int eventCode, Point point)
    {
        TrayEventListener listener = (TrayEventListener) listenerMap.get(String.
            valueOf(uid));
        if (listener != null)
        {
            switch (eventCode)
            {
                case 0:
                    listener.mouseLeftClicked(point);
                    break;
                case 1:
                    listener.mouseRightClicked(point);
                    break;
                case 2:
                    listener.mouseDblClicked(point);
                    break;
                case 3:
                    listener.mouseMove(point);
                    break;
            }
        }
    }

    private native void addTrayIcon0(long handle, TrayIcon info);

    private native void setTrayIcon0(long handle, TrayIcon info,
                                     int modifyField);

    private native void removeTrayIcon0(long handle, int uid);

    /**
     * native 코드에서는 이 TrayIconManager의 인스턴스에 대한 GlobalReference를
     * 전역변수로 선언해놓아야 할 것이다. 그래야 이벤트를 통지받을 수 있다.
     * <p>
     * JNIEnv는 전역변수로 잡혀야하며
     * jobject와 fireTrayEvent의 jmethodID의 GlobalRefenrence가 전역변수로 잡혀야만 한다.
     * 그래야 CallVoidMethod를 수행할 수 있다.
     */
    private native long createTrayHandle(Frame temp);

    /**
     * native 코드에서는 이 TrayIconManager의 인스턴스에 대해 생성된 GlobalReference를
     * 삭제해주어야 할 것이다.
     */
    private native void destroyTrayHandle(long handle);

}
