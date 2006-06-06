/**
 * @(#)Win32Toolkit.java
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
 *   $Id: Win32Toolkit.java,v 1.1 2006/06/06 18:57:45 baojie Exp $
 */
package other.rath.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;

/**
 * win32에서 제공되는 재미있는 기능들을 사용할 수 있는 도구상자클래스이다.
 * <p>
 * 대부분 Windows 계열에서 동작하지만, 특정 메소드들은 Windows 2000에서만
 * 동작할 것이다.
 *
 * @author Jangho Hwang, windrath@hanmail.net
 * @version $Id: Win32Toolkit.java,v 1.1 2006/06/06 18:57:45 baojie Exp $ since 2001/12/05
 */

class NotSupportedPlatformException
    extends RuntimeException
{
    public NotSupportedPlatformException(String msg)
    {
        super(msg);
    }
}

public class Win32Toolkit
{
    private static boolean isLoaded = false;

    /**
     * Win32 toolkit 인스턴스를 생성한다.
     */
    public static synchronized Win32Toolkit getInstance() throws
        NotSupportedPlatformException
    {
        if (!isLoaded)
        {
            String os = System.getProperty("os.name");
            if (!os.startsWith("Windows"))
            {
                throw new NotSupportedPlatformException(os);
            }

            System.loadLibrary("rath_awt");
            isLoaded = true;
        }

        return new Win32Toolkit();
    }

    private Win32Toolkit()
    {

    }

    /**
     * 주어진 컴포넌트를 검은색으로 알파블렌딩하고 transparency의 투명도를 가지도록 변경한다.
     * 이 메소드는 <b>Windows 2000</b>이상에서만 작동할 것이다. <br>
     * 또한 목적 컴포넌트가 Swing일 경우
     * DirectDraw 옵션을 꺼주어야 한다. ddraw 옵션을 끄는 방법은 jvm을 startup할때 환경변수를
     * 주는 것으로 제어할 수 있다.
     * <p>
     * java <b>-Dsun.java2d.noddraw=true</b> AppMain ...
     *
     * @param  comp  투명하게 만들고자 하는 컴포넌트
     * @param  transparency  투명도. 0이면 완전 투명이고, 255면 완전 불투명이다.
     */
    public void makeTransparency(Window comp, int transparency)
    {
        makeTransparency(comp, Color.black, transparency);
    }

    /**
     * 주어진 컴포넌트를 blendColor로 알파블렌딩하고 transparency의 투명도를 가지도록 변경한다.
     * 이 메소드는 <b>Windows 2000</b>이상에서만 작동할 것이다.
     * 또한 목적 컴포넌트가 Swing일 경우
     * DirectDraw 옵션을 꺼주어야 한다. ddraw 옵션을 끄는 방법은 jvm을 startup할때 환경변수를
     * 주는 것으로 제어할 수 있다.
     * <p>
     * java <b>-Dsun.java2d.noddraw=true</b> AppMain ...
     *
     * @param  comp  투명하게 만들고자 하는 컴포넌트
     * @param  blendColor    현재는 사용되지 않는다. (OS에서는 제공한다)
     * @param  transparency  투명도. 0이면 완전 투명이고, 255면 완전 불투명이다.
     */
    public void makeTransparency(Window comp, Color blendColor,
                                 int transparency)
    {
        if (comp == null)
        {
            throw new IllegalArgumentException("comp is null");
        }
        if (blendColor == null)
        {
            blendColor = Color.black;
        }
        if (transparency < -1 || transparency > 255)
        {
            throw new IllegalArgumentException(
                "transparency must be between -1 and 255");
        }

        makeTransparency0(comp, blendColor.getRed(), blendColor.getGreen(),
                          blendColor.getBlue(),
                          transparency);
    }

    private native void makeTransparency0(Window comp, int r, int g, int b,
                                          int transparency);

    /**
     * 주어진 window를 Polygon 영역만 보여지고 나머지 영역은 투명하게 만들어버린다.
     * 한마디로 말하자면 창을 깎는다.
     */
    public void makePolygonRegion(Window window, Polygon p)
    {
        if (window == null)
        {
            throw new IllegalArgumentException("window is null");
        }
        if (p == null)
        {
            throw new IllegalArgumentException("polygon is null");
        }

        makePolygonRegion0(window, p, true);
    }

    private native void makePolygonRegion0(Window window, Polygon p,
                                           boolean redraw);

    public void makeTopMost(Window window, boolean enable)
    {
        makeTopMost0(window, enable);
    }

    private native void makeTopMost0(Window window, boolean enable);

    /**
     * Java의 Image를 가지고 HICON 객체를 생성하여 그 핸들을 넘겨준다.
     * 아이콘이 더이상 필요가 없어지면 반드시 destroyIcon 메소드를 통해서
     * 할당된 리소스를 풀어주어야 한다.
     * <p>
     * 만약 null을 넘긴다면, 0L이 반환되게 될 것이다.
     */
    public long createIconFromImage(Image icon)
    {
        if (icon == null)
        {
            return 0L;
        }

        // 아래의 값은 직접 내 데스크탑에서 SM을 얻어와서 하드코딩하였다.
        int w = 16; // Default icon width    use GetSystemMetrics( SM_CXICON );
        int h = 16; // Default icon height   use GetSystemMetrics( SM_CYICON );

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(icon, 0, 0, w, h, null);
        g.dispose();

        Raster raster = bi.getRaster();
        DataBuffer dataBuffer = raster.getDataBuffer();

        /*
         * 아래의 코드는 sun사의 WFramePeer.java 소스를 참조하여 거의 그대로
         * 사용하였다. 호환을 위한 여러가지 코드를 제거하였고, 성능향상을 위한
         * 간단한 코드를 추가하였다.
         */
        ColorModel alphaCheck = bi.getColorModel();
        //Create icon mask for transparent icons
        //Color icon, so only creating AND mask, not XOR mask
        byte iconmask[] = new byte[ ( (w * h) + 7 / 8)];
        byte tempAND;
        int bufIdx = 0;
        int maskIdx = 0;
        boolean isTransparent = false;

        int bufferSize = dataBuffer.getSize();

        for (bufIdx = 0, maskIdx = 0;
             bufIdx < bufferSize && maskIdx < iconmask.length;
             maskIdx++)
        {
            tempAND = 0;
            for (int bitIdx = 0; bitIdx < 8 && bufIdx < bufferSize; bitIdx++)
            {
                //This seems wrong - shouldn't it be masked if alpha
                //ISN'T 0? 이점은 나또한 이상하게 생각한다.
                //인간적으로 밑의 코드는 하나도 이해할 수 없다 -.-
                if (alphaCheck.getAlpha(dataBuffer.getElem(bufIdx++)) == 0)
                {
                    isTransparent = true;
                    tempAND |= (byte) 0x1;
                }
                else
                {
                    tempAND &= (byte) 0xFE;
                }

                if (bitIdx < 7)
                {
                    tempAND = (byte) (tempAND << 1);
                }
            }
            iconmask[maskIdx] = tempAND;
        }

        if (!isTransparent)
        {
            iconmask = null;
        }

        /*
         * Scansize를 구하는 코드는 현재 제거되었다. 필요하다면,
         * BufferedImage.getSampleModel로 SampleModel을 얻어온 후
         * 이것을 SinglePixelPackedSampleModel로 cast하여 getScanlineStride 메소드를
         * 사용하도록 하라.
         */
        return createIconFromRaster0(
            ( (DataBufferInt) dataBuffer).getData(), iconmask,
            raster.getWidth(), raster.getWidth(), raster.getHeight());
    }

    /**
     * 주어진 데이터로부터 HICON을 생성하여, 그 핸들의 포인터를 반환한다.
     */
    private native long createIconFromRaster0(int[] data, byte[] maskData,
                                              int ss, int width, int height);

    /**
     * 아이콘 핸들을 메모리에서 제거한다.
     */
    public native void destroyIcon(long iconHandle);
};
