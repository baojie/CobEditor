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
 *    $Id: TrayIcon.java,v 1.1 2006/06/13 03:11:10 baojie Exp $
 */
package other.rath.tools.tray;

import java.awt.Image;
import java.io.UnsupportedEncodingException;
/**
 * Ʈ���̾��������� ����ؾ��� �������� ���� Ŭ�����̴�.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: TrayIcon.java,v 1.1 2006/06/13 03:11:10 baojie Exp $, since 2001/12/17
 */
public class TrayIcon
{
	/**
	 * Ʈ���̾����� ����� ���Ǹ�, Icon�� ������ ����Ǿ�� �Ҷ� ���Ǵ� �ʵ��̴�.
	 */
	public static final int TYPE_ICON        = 0x00000002;
	/**
	 * Ʈ���̾����� ����� ���Ǹ�, ������ ������ ����Ǿ�� �Ҷ� ���Ǵ� �ʵ��̴�.
	 */
	public static final int TYPE_TIP         = 0x00000004;

	private int uid = -1;
	private NativeIcon icon = null;
	private String tip = null;

	/**
	 * �ش� icon���� ������ Ʈ���̾����� ��ü�� �����Ѵ�.
	 */
	public TrayIcon( NativeIcon icon )
	{
		this( icon, null );
	}

	/**
	 * �ش� icon�� tooltip(���콺�� �ø��� ǥ�õǴ�)�� ������ Ʈ���̾����� ��ü�� 
	 * �����Ѵ�.
	 */
	public TrayIcon( NativeIcon icon, String tip )
	{
		setIcon( icon );
		setToolTip( tip );
	}

	/**
	 * Shell_NotifyIcon �Լ��� �� Ʈ���̾������� ������ ���� �����ڸ� �����Ѵ�.
	 * �� ���� TrayIconManager�� ���� �����Ǵ� ������ �Ժη� �����ؼ��� �ȵȴ�.
	 */
	void setUniqueID( int uid )
	{
		this.uid = uid;
	}

	/**
	 * �� Ʈ���̾������� �����ϴ� ���� �����ڸ� ���´�.
	 */
	int getUniqueID()
	{
		return this.uid;
	}

	/**
	 * �־��� icon �̹����� Ʈ���� �������� �����Ѵ�. �� �޼ҵ带 ȣ���ϱ⸸ �ϸ� 
	 * �Ǵ� ���� �ƴ϶�, 
	 * {@link TrayIconManager#modifyTrayIcon(rath.tools.tray.TrayIcon,int) TrayIconManager.modifyTrayIcon}
	 * �޼ҵ带 ���� ����� ����� �˷��־�߸� ����ȴ�.
	 */
	public void setIcon( NativeIcon icon )
	{
		if( icon==null )
			throw new IllegalArgumentException( "icon is null" );

		this.icon = icon;
	}

	/**
	 * ���� ������ �������� ��ȯ�Ѵ�.
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
	 * �����ܿ� ���콺�� �÷�������, ǥ�õ� ���� �ؽ�Ʈ�� �����Ѵ�.
	 * {@link #setIcon(rath.tools.tray.NativeIcon) setIcon} �޼ҵ�ó�� 
	 * {@link TrayIconManager#modifyTrayIcon(rath.tools.tray.TrayIcon,int) TrayIconManager.modifyTrayIcon} 
	 * �޼ҵ带 ���� ����� ����� �˷��־�߸� ����ȴ�.
	 */
	public void setToolTip( String tip )
	{
		this.tip = tip;
	}

	/**
	 * ���� �����Ǿ��ִ� ���� �ؽ�Ʈ�� �����´�.
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
	 * ���� tooltip �޽����� �ý����� default encoding set�� ����Ͽ� byte[]��
	 * ��ȯ��Ų ���� ��ȯ�Ѵ�.
	 */
	public byte[] getToolTipBytes() throws UnsupportedEncodingException
	{
		return getBytes( this.tip );
	}

	/**
	 * ���� tooltip �޽����� �־��� encoding set�� ����Ͽ� byte[]�� 
	 * ��ȯ��Ų ���� ��ȯ�Ѵ�.
	 */
	public byte[] getToolTipBytes( String enc ) throws UnsupportedEncodingException
	{
		return getBytes( this.tip, enc );
	}
}