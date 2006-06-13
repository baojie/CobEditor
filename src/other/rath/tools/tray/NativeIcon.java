/*
 * @(#)NativeIcon.java
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
 *    $Id: NativeIcon.java,v 1.1 2006/06/13 03:11:10 baojie Exp $
 */
package other.rath.tools.tray;

import java.awt.Image;
import javax.swing.ImageIcon;
/**
 * 크리스마스 이브에 클래스나 만들고 있다니 한심할뿐이다 -_-.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: NativeIcon.java,v 1.1 2006/06/13 03:11:10 baojie Exp $ since at 2001/12/24
 */
public class NativeIcon 
{
	private Image icon = null;
	private long iconHandle = 0L;

	/**
	 * 해당 파일로부터 NativeIcon 객체를 생성한다.
	 */
	public NativeIcon( String filename )
	{
		setImage( filename );
	}

	/**
	 * 해당 이미지로부터 NativeIcon 객체를 생성한다.
	 */
	public NativeIcon( Image icon )
	{
		setImage( icon );
	}

	/**
	 * 해당 파일로부터 아이콘 이미지를 변경한다.
	 */
	public void setImage( String filename )
	{
		this.icon = new ImageIcon(filename).getImage();
		this.iconHandle = 0L;
	}

	/**
	 * 해당 이미지로부터 아이콘 이미지를 변경한다.
	 */
	public void setImage( Image image )
	{
		if( image==null )
			throw new IllegalArgumentException( "image is null" );

		this.icon = image;
		this.iconHandle = 0L;
	}

	/**
	 * 현재 설정된 이미지를 반환한다.
	 */
	public Image getImage()
	{
		return this.icon;
	}

	/**
	 * 이 아이콘에 대한 native 핸들이 생성되었는지 확인한다.
	 */
	boolean isHandleCreated()
	{
		return this.iconHandle!=0L;
	}

	/**
	 * 현재 설정되어있는 아이콘의 핸들을 설정한다. 이 값은
	 * {@link rath.tools.Win32Toolkit#createIconFromImage(java.awt.Image) 
	 * Win32Toolkit.createIconFromImage} 메소드를 통해
	 * 얻어온 핸들이여야만 한다.
	 */
	void setIconHandle( long handle )
	{
		this.iconHandle = handle;
	}

	/**
	 * 이 아이콘의 native 아이콘 핸들을 반환한다.
	 */
	long getIconHandle()
	{
		return this.iconHandle;
	}

	/**
	 * 이미지를 더이상 사용하지 않는다면, 이 메소드를 통해 관련 리소스들을
	 * 풀어주는 것이 좋다.
	 */
	public void flush()
	{
		if( icon!=null )
			icon.flush();
		this.iconHandle = 0L;
	}
}