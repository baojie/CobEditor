/*
 * @(#)TrayEventListener.java
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
 *    $Id: TrayEventListener.java,v 1.1 2006/06/13 03:11:10 baojie Exp $
 */
package other.rath.tools.tray;

import java.awt.Point;
import java.util.EventListener;
/**
 * 트레이아이콘에서 일어나는 이벤트들을 청취하는 이벤트리스너 인터페이스이다.
 * 
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: TrayEventListener.java,v 1.1 2006/06/13 03:11:10 baojie Exp $, since 2001/12/17
 */
public interface TrayEventListener extends EventListener
{
	/**
	 * 아이콘 영역에서 마우스 왼쪽 버튼을 눌렀을때.
	 * 마우스의 좌표값이 함께 넘어온다.
	 */
	public abstract void mouseLeftClicked( Point p );

	/**
	 * 아이콘 영역에서 마우스 오른쪽 버튼을 눌렀을때.
	 * 마우스의 좌표값이 함께 넘어온다.
	 */
	public abstract void mouseRightClicked( Point p );

	/**
	 * 아이콘 영역에서 마우스 왼쪽 버튼을 더블클릭했을 때.
	 * 마우스의 좌표값이 함께 넘어온다.
	 */
	public abstract void mouseDblClicked( Point p );

	/**
	 * 아이콘 영역에서 마우스를 움직였을 때.
	 * 마우스의 좌표값이 함께 넘어온다.
	 */
	public abstract void mouseMove( Point p );
}