/**
 * @(#)AdvancedTrayIcon.java
 *
 * Copyright (c) 2001, JangHo Hwang
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
 *    $Id: AdvancedTrayIcon.java,v 1.1 2006/06/06 18:57:28 baojie Exp $
 */
package other.rath.tools.tray;

import java.io.UnsupportedEncodingException;

/**
 * Shell32.dll version 5.0 �̻󿡼��� ��ȿ�� �ý��� Ʈ���̿�����
 * ǳ�����򸻵��� �߰������� ��Ÿ���� ������ ����ϴ� Ʈ���� ���� Ŭ�����̴�.
 * <p>
 * �� Ŭ������ ���� Ʈ���̿� ����� �ϰ� �Ǹ�, �ش� ǳ�� ������ ��� ������
 * �ϴ� ���, ǳ�������� ��Ÿ�� ���̸�, �÷������� �ٸ�����, �� 10�� �Ŀ�
 * �ڵ����� ����� ���̴�. ������ �ʿ伺�� ����������
 * {@link TrayIconManager#modifyTrayIcon(rath.tools.tray.TrayIcon,int)
 * TrayIconManager.modifyTrayIcon} �޼ҵ带 ���Ͽ� ���� ����� �������־�� �Ѵ�.
 * <p>
 * Shell32.dll�� �⺻������ Windows ME/2000���� �⺻������ �����Ǿ��ִ� dll�̴�.
 * �׷��Ƿ� AdvancedTrayIconInfo�� Windows ME/2000 ������ ��밡�� �� Ŭ�����̴�.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: AdvancedTrayIcon.java,v 1.1 2006/06/06 18:57:28 baojie Exp $, since 2001/12/19
 */
public class AdvancedTrayIcon
    extends TrayIcon
{
    /**
     * ǳ������ ����� ��Ÿ���� int�� ����̴�.
     */
    public static final int TYPE_BALOON = 0x00000010;
    /**
     * ǳ�� ���� ������ ���� �������� ���� �ʰڴٴ� ����̴�.
     */
    public static final int ICON_NONE = 0;
    /**
     * ǳ�� ���� ������ ���� ���� ������(����ǥ)�� ��Ÿ���� ����̴�.
     */
    public static final int ICON_INFORMATION = 1;
    /**
     * ǳ�� ���� ������ ���� ��� �������� ��Ÿ���� ����̴�.
     */
    public static final int ICON_WARNING = 2;
    /**
     * ǳ�� ���� ������ ���� ���� ������(Xǥ��)�� ��Ÿ���� ����̴�.
     */
    public static final int ICON_ERROR = 3;

    private String baloonText = ""; // Default value
    private String baloonTitle = ""; // Default value
    private int infoFlag = ICON_NONE; // Default value

    public AdvancedTrayIcon(NativeIcon icon)
    {
        super(icon);
    }

    public AdvancedTrayIcon(NativeIcon icon, String tip)
    {
        super(icon, tip);
    }

    /**
     * ���� ǳ�� ������ ������ ���´�.
     */
    public String getBaloonText()
    {
        return this.baloonText;
    }

    /**
     * ���� ǳ�� ������ ������ system default encoding�� �̿��Ͽ�
     * byte[] ���·� ��ȯ�Ͽ� ��ȯ�Ѵ�.
     */
    public byte[] getBaloonTextBytes() throws UnsupportedEncodingException
    {
        return super.getBytes(this.baloonText);
    }

    /**
     * ǳ�� ���� ������ �����Ѵ�.
     */
    public void setBaloonText(String text)
    {
        if (text == null)
        {
            text = "";
        }
        this.baloonText = text;
    }

    /**
     * ���� ������ ǳ�� ���� ������ ���´�.
     */
    public String getBaloonTitle()
    {
        return this.baloonTitle;
    }

    /**
     * ���� ǳ�� ������ ������ system default encoding�� �̿��Ͽ�
     * byte[] ���·� ��ȯ�Ͽ� ��ȯ�Ѵ�.
     */
    public byte[] getBaloonTitleBytes() throws UnsupportedEncodingException
    {
        return super.getBytes(this.baloonTitle);
    }

    /**
     * ǳ�� ������ ������ �����Ѵ�. �� ������ ǳ�� ������ popup �Ͽ�����
     * ��ܿ� <b>Bold</b> ���·� ���� ������ �ٷ� ���� ��Ÿ���� �����̴�.
     */
    public void setBaloonTitle(String title)
    {
        if (title == null)
        {
            title = "";
        }
        this.baloonTitle = title;
    }

    /**
     * ǳ�� ������ ���� ������ ���׸��� �������� �ϳ� ���� �� �ִµ�, �� ����
     * �����Ѵ�. �� �����δ� ICON_NONE, ICON_INFO, ICON_WARNING, ICON_ERROR ��
     * ���� �� ������, default ���� ICON_NONE�̴�.
     */
    public void setBaloonIcon(int icon)
    {
        this.infoFlag = icon;
    }

    /**
     * ���� ������ ǳ�� ���� ������ �ڵ带 �����´�. �� ����
     * ICON_NONE, ICON_INFO, ICON_WARNING, ICON_ERROR ���� �ϳ��� �� ���̴�.
     */
    public int getBaloonIcon()
    {
        return this.infoFlag;
    }

    /**
     * �� Ʈ���̾����� Ŭ������ ���� �÷������� ��밡���� Ŭ��������
     * �����Ͽ� �ش�.
     */
    private boolean isAvailablePlatform()
    {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows me") ||
            os.startsWith("windows 20") ||
            os.startsWith("windows wh") ||
            os.startsWith("windows xp"))
        {
            return true;
        }
        return false;
    }

}
