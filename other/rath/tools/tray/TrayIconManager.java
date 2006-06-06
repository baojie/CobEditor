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
 *    $Id: TrayIconManager.java,v 1.1 2006/06/06 18:57:28 baojie Exp $
 */
package other.rath.tools.tray;

import java.util.HashMap;
import java.util.Iterator;

import java.awt.Frame;
import java.awt.Point;

import other.rath.tools.Win32Toolkit;

/**
 * Ʈ���� �����ܵ��� �������ִ� Ŭ�����̴�.
 * �� Ŭ������ ������ �ν��Ͻ� ������ ������� �ʴ´�.
 * <p>
 * ���� Ʈ���� �������� ������ �����Ͽ�����, �ý��� Ʈ���̿� �������� �״��
 * �����ִ� ��찡 ����. �̰��� �����ϱ� ���� Runtime.addShutdownHook �Լ��� ����Ѵ�.
 * �̰��� ����ϱ� ������, JDK 1.3 �̻��� �ʿ�� �Ѵ�.
 * <p>
 * ��� Platform�� Windows 95/98/ME�� NT 4.0/2000 �̻��� ��� �����Ѵ�.
 * <br>��, ǳ������ ����� �̿��ϱ� ���� AdvancedTrayIcon�� ����Ϸ� �Ѵٸ�,
 * Windows ME/2000 �̻��� �÷����̿��߸� �Ѵ�. (Shell32.dll version 5.0�� �ʿ��ϴ�.
 * ME�� 2000���� �⺻������ 5.0 ������ Shell32.dll �� ���ԵǾ� �ִ�)
 * <p>
 * �Ʒ��� ������ ���� ���� ������� ���� �� ���� ���̴�.
 * <p>
 * <pre><code>
 * Win32Toolkit toolkit = Win32Toolkit.getInstance();
 * ...
 * TrayIconManager tray = new TrayIconManager( tookit );
 *
 * <font color=green><i>// ǳ�������� ������ Ʈ���̾����� ��ü�� �����Ѵ�.</font></i>
 * AdvancedTrayIcon icon = new AdvancedTrayIcon(
 *     new ImageIcon("cute.gif").getImage(), "�̻�Ʈ����" );
 *
 * <font color=green><i>// ǳ�������� ������ �����Ѵ�.</font></i>
 * icon.setBaloonTitle( "���� ���� ������" );
 * <font color=green><i>// ǳ������ ������ ���Ѵ�. ���๮��(\n)�� ����ȴ�.</font></i>
 * icon.setBaloonText( "�������� ���� �Ͽ����ϴ�.\n��� �ڸ����� �Ͼ�ּ���" );
 * <font color=green><i>// ǳ������ ���� ���� ǥ���� �������� �����Ѵ�.</font></i>
 * icon.setBaloonIcon( icon.ICON_INFORMATION );
 *
 * <font color=green><i>// �̺�Ʈ�����ʿ� �Բ�, �ý��� Ʈ���̿� ������ ����Ѵ�.</font></i>
 * tray.addTrayIcon( icon, new TrayEventAdapter() {
 *     public void mouseDblClicked( Point p )
 *     {
 *         System.out.println( p + " ��ǥ���� ����Ŭ���ߴ�." );
 *     }
 * });
 *
 * <font color=green><i>// �߰� ���� ����� �׽�Ʈ �ϱ� ���� 10�ʸ� ����.</font></i>
 * Thread.currentThread().sleep( 10000L );
 *
 * <font color=green><i>// ������ ������ �����Ѵ�.</font></i>
 * icon.setIcon( new ImageIcon("angry.gif").getImage() );
 * icon.setBaloonTitle( "������ �����ϱ� �ֳ�" );
 * icon.setBaloonIcon( icon.ICON_ERROR );
 *
 * <font color=green><i>// ����� �ʵ������� �Բ� ������ ���泻���� �����Ų��.</font></i>
 * tray.modifyTrayIcon( info, TrayIcon.TYPE_ICON | TypeIcon.TYPE_BALOON );
 *
 * </code></pre>
 *
 * Windows 95/98 �� NT 4.0�̻� ������� ��� ǳ�����򸻸� ������� ���һ� �Ϲ�
 * ����� ��� ����� �� �ִ�. ��, {@link AdvancedTrayIcon AdvancedTrayIcon}�� �ƴ�
 * {@link TrayIcon TrayIcon} Ŭ������ ����ؾ� �� ���̴�.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: TrayIconManager.java,v 1.1 2006/06/06 18:57:28 baojie Exp $, since 2001/12/17
 */
public class TrayIconManager
{
    private final Win32Toolkit toolkit;
    private int uniqueId = 0;
    private long handle = 0L;
    private HashMap listenerMap = null;

    /**
     * Windows�� �ý��� Ʈ���̸� ����� �� �ְ� ���ִ� TrayIconManager �ν��Ͻ���
     * �����Ѵ�. Windows ���� ����̱� ������, Win32Toolkit �ν��Ͻ��� �Ѱ��־�� �Ѵ�.
     */
    public TrayIconManager(Win32Toolkit toolkit)
    {
        this.toolkit = toolkit;
        this.listenerMap = new HashMap();
    }

    /**
     * �ý���Ʈ���̿����� ���ο� Ʈ���� �������� ����Ѵ�.
     *
     * @param  info     ���ο� Ʈ���� �������� ������ ���� ��ü
     * @param  listener ��ϵ� Ʈ���� ������ ���� �̺�Ʈ�� û���� �̺�Ʈ������
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
     * ������ ��ϵǾ��ִ� Ʈ���̾������� �Ӽ��� �����Ѵ�.
     * <p>
     * ����: TrayIconInfo ��ü�� ���� �����ϸ� �ȵǸ�, �ݵ�� addTrayIcon�� ����־���
     * ��ü���� �Ӽ��� �����Ͽ��� �Ѵ�. �׷��� ������ IllegalArgumentException�� ���� ���̴�.
     *
     * @param  info        ����� ������ ������ �ִ� ���� TrayIconInfo ��ü
     * @param  modifyField TrayIconInfo.ICON, TrayIconInfo.TIP �� ������ ���� ����
     *                     �ʵ���� �� ��. (ex: <b>TrayIconInfo.ICON | TrayIconInfo.TIP</b> )
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
     * �ش� Ʈ���� �������� �ý��� Ʈ���̿��� �����Ѵ�.
     * ���� ������� ���� TrayIconInfo ��ü�� �����Ϸ� �Ѵٸ�,
     * java.lang.IllegalArgumentException�� ���� ���̴�.
     */
    public synchronized void removeTrayIcon(TrayIcon info)
    {
        // getUniqueID�� HWND�� Shell_NotifyIcon( NIM_REMOVE )�� �����ϵ��� �Ѵ�.
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
     * Ư�� NativeIcon�� ���̻� �ʿ����� ������, Java image�� os specify �ϰ�
     * ������ ������ �ڵ��� ��� �������ش�.
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
     * native WindowProc ���κ��� ȣ��Ǵ� �̺�Ʈ �߼� �޼ҵ��̴�.
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
     * native �ڵ忡���� �� TrayIconManager�� �ν��Ͻ��� ���� GlobalReference��
     * ���������� �����س��ƾ� �� ���̴�. �׷��� �̺�Ʈ�� �������� �� �ִ�.
     * <p>
     * JNIEnv�� ���������� �������ϸ�
     * jobject�� fireTrayEvent�� jmethodID�� GlobalRefenrence�� ���������� �����߸� �Ѵ�.
     * �׷��� CallVoidMethod�� ������ �� �ִ�.
     */
    private native long createTrayHandle(Frame temp);

    /**
     * native �ڵ忡���� �� TrayIconManager�� �ν��Ͻ��� ���� ������ GlobalReference��
     * �������־�� �� ���̴�.
     */
    private native void destroyTrayHandle(long handle);

}
