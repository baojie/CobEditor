/*
 * @(#)TrayIcon.java
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
 *    $Id: TrayIcon.java,v 1.1 2006/06/06 18:57:28 baojie Exp $
 */
package other.rath.tools.tray;

import java.awt.Image;
import java.io.UnsupportedEncodingException;
/**
 * 트레이아이콘으로 등록해야할 정보들을 담은 클래스이다.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: TrayIcon.java,v 1.1 2006/06/06 18:57:28 baojie Exp $, since 2001/12/17
 */
public class TrayIcon
{
	/**
	 * 트레이아이콘 변경시 사용되며, Icon의 변경이 적용되어야 할때 사용되는 필드이다.
	 */
	public static final int TYPE_ICON        = 0x00000002;
	/**
	 * 트레이아이콘 변경시 사용되며, 툴팁의 변경이 적용되어야 할때 사용되는 필드이다.
	 */
	public static final int TYPE_TIP         = 0x00000004;

	private int uid = -1;
	private NativeIcon icon = null;
	private String tip = null;

	/**
	 * 해당 icon만을 가지는 트레이아이콘 객체를 생성한다.
	 */
	public TrayIcon( NativeIcon icon )
	{
		this( icon, null );
	}

	/**
	 * 해당 icon과 tooltip(마우스를 올리면 표시되는)을 가지는 트레이아이콘 객체를 
	 * 생성한다.
	 */
	public TrayIcon( NativeIcon icon, String tip )
	{
		setIcon( icon );
		setToolTip( tip );
	}

	/**
	 * Shell_NotifyIcon 함수가 각 트레이아이콘을 구분할 고유 구분자를 설정한다.
	 * 이 값은 TrayIconManager를 통해 설정되는 값으로 함부로 변경해서는 안된다.
	 */
	void setUniqueID( int uid )
	{
		this.uid = uid;
	}

	/**
	 * 이 트레이아이콘을 구분하는 고유 구분자를 얻어온다.
	 */
	int getUniqueID()
	{
		return this.uid;
	}

	/**
	 * 주어진 icon 이미지로 트레이 아이콘을 변경한다. 이 메소드를 호출하기만 하면 
	 * 되는 것이 아니라, 
	 * {@link TrayIconManager#modifyTrayIcon(rath.tools.tray.TrayIcon,int) TrayIconManager.modifyTrayIcon}
	 * 메소드를 통해 변경된 사실을 알려주어야만 적용된다.
	 */
	public void setIcon( NativeIcon icon )
	{
		if( icon==null )
			throw new IllegalArgumentException( "icon is null" );

		this.icon = icon;
	}

	/**
	 * 현재 설정된 아이콘을 반환한다.
	 */
	public NativeIcon getIcon()
	{
		return this.icon;
	}

	long getIconHandle()
	{
		return this.icon.getIconHandle();
	}

	/**
	 * 아이콘에 마우스를 올려놨을때, 표시될 툴팁 텍스트를 설정한다.
	 * {@link #setIcon(rath.tools.tray.NativeIcon) setIcon} 메소드처럼 
	 * {@link TrayIconManager#modifyTrayIcon(rath.tools.tray.TrayIcon,int) TrayIconManager.modifyTrayIcon} 
	 * 메소드를 통해 변경된 사실을 알려주어야만 적용된다.
	 */
	public void setToolTip( String tip )
	{
		this.tip = tip;
	}

	/**
	 * 현재 설정되어있는 툴팁 텍스트를 가져온다.
	 */
	public String getToolTip()
	{
		return this.tip;
	}

	protected byte[] getBytes( String str ) throws UnsupportedEncodingException
	{
		return getBytes( str, System.getProperty("file.encoding") );
	}

	protected byte[] getBytes( String str, String enc ) throws UnsupportedEncodingException
	{
		if( str==null )
			return null;
		return str.getBytes(enc);
	}

	/**
	 * 현재 tooltip 메시지를 시스템의 default encoding set을 사용하여 byte[]로
	 * 변환시킨 값을 반환한다.
	 */
	public byte[] getToolTipBytes() throws UnsupportedEncodingException
	{
		return getBytes( this.tip );
	}

	/**
	 * 현재 tooltip 메시지를 주어진 encoding set을 사용하여 byte[]로 
	 * 변환시킨 값을 반환한다.
	 */
	public byte[] getToolTipBytes( String enc ) throws UnsupportedEncodingException
	{
		return getBytes( this.tip, enc );
	}
}