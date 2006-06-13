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
 *    $Id: AdvancedTrayIcon.java,v 1.1 2006/06/13 03:11:10 baojie Exp $
 */
package other.rath.tools.tray;

import java.io.UnsupportedEncodingException;

/**
 * Shell32.dll version 5.0 이상에서만 유효한 시스템 트레이에서의
 * 풍선도움말등의 추가정보를 나타내고 싶을때 사용하는 트레이 정보 클래스이다.
 * <p>
 * 이 클래스를 통해 트레이에 등록을 하게 되면, 해당 풍선 도움말은 등록 수행을
 * 하는 즉시, 풍선도움말이 나타날 것이며, 플랫폼마다 다르지만, 약 10초 후에
 * 자동으로 사라질 것이다. 보여줄 필요성이 있을때마다
 * {@link TrayIconManager#modifyTrayIcon(rath.tools.tray.TrayIcon,int)
 * TrayIconManager.modifyTrayIcon} 메소드를 통하여 변동 사실을 통지해주어야 한다.
 * <p>
 * Shell32.dll은 기본적으로 Windows ME/2000에만 기본적으로 배포되어있는 dll이다.
 * 그러므로 AdvancedTrayIconInfo는 Windows ME/2000 에서만 사용가능 한 클래스이다.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: AdvancedTrayIcon.java,v 1.1 2006/06/13 03:11:10 baojie Exp $, since 2001/12/19
 */
public class AdvancedTrayIcon
    extends TrayIcon
{
    /**
     * 풍선도움말 기능을 나타내는 int형 상수이다.
     */
    public static final int TYPE_BALOON = 0x00000010;
    /**
     * 풍선 도움말 제목의 옆에 아이콘을 넣지 않겠다는 상수이다.
     */
    public static final int ICON_NONE = 0;
    /**
     * 풍선 도움말 제목의 옆에 정보 아이콘(느낌표)를 나타내는 상수이다.
     */
    public static final int ICON_INFORMATION = 1;
    /**
     * 풍선 도움말 제목의 옆에 경고 아이콘을 나타내는 상수이다.
     */
    public static final int ICON_WARNING = 2;
    /**
     * 풍선 도움말 제목의 옆에 오류 아이콘(X표시)를 나타내는 상수이다.
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
     * 현재 풍선 도움말의 내용을 얻어온다.
     */
    public String getBaloonText()
    {
        return this.baloonText;
    }

    /**
     * 현재 풍선 도움말의 내용을 system default encoding을 이용하여
     * byte[] 형태로 변환하여 반환한다.
     */
    public byte[] getBaloonTextBytes() throws UnsupportedEncodingException
    {
        return super.getBytes(this.baloonText);
    }

    /**
     * 풍선 도움말 내용을 설정한다.
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
     * 현재 설정된 풍선 도움말 제목을 얻어온다.
     */
    public String getBaloonTitle()
    {
        return this.baloonTitle;
    }

    /**
     * 현재 풍선 도움말의 제목을 system default encoding을 이용하여
     * byte[] 형태로 변환하여 반환한다.
     */
    public byte[] getBaloonTitleBytes() throws UnsupportedEncodingException
    {
        return super.getBytes(this.baloonTitle);
    }

    /**
     * 풍선 도움말의 제목을 설정한다. 이 제목은 풍선 도움말이 popup 하였을때
     * 상단에 <b>Bold</b> 형태로 제목 아이콘 바로 옆에 나타나는 제목이다.
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
     * 풍선 도움말의 제목 좌측에 조그마한 아이콘을 하나 넣을 수 있는데, 그 값을
     * 설정한다. 이 값으로는 ICON_NONE, ICON_INFO, ICON_WARNING, ICON_ERROR 가
     * 사용될 수 있으며, default 값은 ICON_NONE이다.
     */
    public void setBaloonIcon(int icon)
    {
        this.infoFlag = icon;
    }

    /**
     * 현재 설정된 풍선 도움말 아이콘 코드를 가져온다. 이 값은
     * ICON_NONE, ICON_INFO, ICON_WARNING, ICON_ERROR 중의 하나가 될 것이다.
     */
    public int getBaloonIcon()
    {
        return this.infoFlag;
    }

    /**
     * 이 트레이아이콘 클래스가 현재 플랫폼에서 사용가능한 클래스인지
     * 조사하여 준다.
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
