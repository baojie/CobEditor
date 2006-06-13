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
 * ũ�������� �̺꿡 Ŭ������ ����� �ִٴ� �ѽ��һ��̴� -_-.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: NativeIcon.java,v 1.1 2006/06/13 03:11:10 baojie Exp $ since at 2001/12/24
 */
public class NativeIcon 
{
	private Image icon = null;
	private long iconHandle = 0L;

	/**
	 * �ش� ���Ϸκ��� NativeIcon ��ü�� �����Ѵ�.
	 */
	public NativeIcon( String filename )
	{
		setImage( filename );
	}

	/**
	 * �ش� �̹����κ��� NativeIcon ��ü�� �����Ѵ�.
	 */
	public NativeIcon( Image icon )
	{
		setImage( icon );
	}

	/**
	 * �ش� ���Ϸκ��� ������ �̹����� �����Ѵ�.
	 */
	public void setImage( String filename )
	{
		this.icon = new ImageIcon(filename).getImage();
		this.iconHandle = 0L;
	}

	/**
	 * �ش� �̹����κ��� ������ �̹����� �����Ѵ�.
	 */
	public void setImage( Image image )
	{
		if( image==null )
			throw new IllegalArgumentException( "image is null" );

		this.icon = image;
		this.iconHandle = 0L;
	}

	/**
	 * ���� ������ �̹����� ��ȯ�Ѵ�.
	 */
	public Image getImage()
	{
		return this.icon;
	}

	/**
	 * �� �����ܿ� ���� native �ڵ��� �����Ǿ����� Ȯ���Ѵ�.
	 */
	boolean isHandleCreated()
	{
		return this.iconHandle!=0L;
	}

	/**
	 * ���� �����Ǿ��ִ� �������� �ڵ��� �����Ѵ�. �� ����
	 * {@link rath.tools.Win32Toolkit#createIconFromImage(java.awt.Image) 
	 * Win32Toolkit.createIconFromImage} �޼ҵ带 ����
	 * ���� �ڵ��̿��߸� �Ѵ�.
	 */
	void setIconHandle( long handle )
	{
		this.iconHandle = handle;
	}

	/**
	 * �� �������� native ������ �ڵ��� ��ȯ�Ѵ�.
	 */
	long getIconHandle()
	{
		return this.iconHandle;
	}

	/**
	 * �̹����� ���̻� ������� �ʴ´ٸ�, �� �޼ҵ带 ���� ���� ���ҽ�����
	 * Ǯ���ִ� ���� ����.
	 */
	public void flush()
	{
		if( icon!=null )
			icon.flush();
		this.iconHandle = 0L;
	}
}