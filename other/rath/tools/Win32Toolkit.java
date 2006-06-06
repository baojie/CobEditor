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
 * win32���� �����Ǵ� ����ִ� ��ɵ��� ����� �� �ִ� ��������Ŭ�����̴�.
 * <p>
 * ��κ� Windows �迭���� ����������, Ư�� �޼ҵ���� Windows 2000������
 * ������ ���̴�.
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
     * Win32 toolkit �ν��Ͻ��� �����Ѵ�.
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
     * �־��� ������Ʈ�� ���������� ���ĺ����ϰ� transparency�� ������ �������� �����Ѵ�.
     * �� �޼ҵ�� <b>Windows 2000</b>�̻󿡼��� �۵��� ���̴�. <br>
     * ���� ���� ������Ʈ�� Swing�� ���
     * DirectDraw �ɼ��� ���־�� �Ѵ�. ddraw �ɼ��� ���� ����� jvm�� startup�Ҷ� ȯ�溯����
     * �ִ� ������ ������ �� �ִ�.
     * <p>
     * java <b>-Dsun.java2d.noddraw=true</b> AppMain ...
     *
     * @param  comp  �����ϰ� ������� �ϴ� ������Ʈ
     * @param  transparency  ����. 0�̸� ���� �����̰�, 255�� ���� �������̴�.
     */
    public void makeTransparency(Window comp, int transparency)
    {
        makeTransparency(comp, Color.black, transparency);
    }

    /**
     * �־��� ������Ʈ�� blendColor�� ���ĺ����ϰ� transparency�� ������ �������� �����Ѵ�.
     * �� �޼ҵ�� <b>Windows 2000</b>�̻󿡼��� �۵��� ���̴�.
     * ���� ���� ������Ʈ�� Swing�� ���
     * DirectDraw �ɼ��� ���־�� �Ѵ�. ddraw �ɼ��� ���� ����� jvm�� startup�Ҷ� ȯ�溯����
     * �ִ� ������ ������ �� �ִ�.
     * <p>
     * java <b>-Dsun.java2d.noddraw=true</b> AppMain ...
     *
     * @param  comp  �����ϰ� ������� �ϴ� ������Ʈ
     * @param  blendColor    ����� ������ �ʴ´�. (OS������ �����Ѵ�)
     * @param  transparency  ����. 0�̸� ���� �����̰�, 255�� ���� �������̴�.
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
     * �־��� window�� Polygon ������ �������� ������ ������ �����ϰ� ����������.
     * �Ѹ���� �����ڸ� â�� ��´�.
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
     * Java�� Image�� ������ HICON ��ü�� �����Ͽ� �� �ڵ��� �Ѱ��ش�.
     * �������� ���̻� �ʿ䰡 �������� �ݵ�� destroyIcon �޼ҵ带 ���ؼ�
     * �Ҵ�� ���ҽ��� Ǯ���־�� �Ѵ�.
     * <p>
     * ���� null�� �ѱ�ٸ�, 0L�� ��ȯ�ǰ� �� ���̴�.
     */
    public long createIconFromImage(Image icon)
    {
        if (icon == null)
        {
            return 0L;
        }

        // �Ʒ��� ���� ���� �� ����ũž���� SM�� ���ͼ� �ϵ��ڵ��Ͽ���.
        int w = 16; // Default icon width    use GetSystemMetrics( SM_CXICON );
        int h = 16; // Default icon height   use GetSystemMetrics( SM_CYICON );

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(icon, 0, 0, w, h, null);
        g.dispose();

        Raster raster = bi.getRaster();
        DataBuffer dataBuffer = raster.getDataBuffer();

        /*
         * �Ʒ��� �ڵ�� sun���� WFramePeer.java �ҽ��� �����Ͽ� ���� �״��
         * ����Ͽ���. ȣȯ�� ���� �������� �ڵ带 �����Ͽ���, ��������� ����
         * ������ �ڵ带 �߰��Ͽ���.
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
                //ISN'T 0? ������ ������ �̻��ϰ� �����Ѵ�.
                //�ΰ������� ���� �ڵ�� �ϳ��� ������ �� ���� -.-
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
         * Scansize�� ���ϴ� �ڵ�� ���� ���ŵǾ���. �ʿ��ϴٸ�,
         * BufferedImage.getSampleModel�� SampleModel�� ���� ��
         * �̰��� SinglePixelPackedSampleModel�� cast�Ͽ� getScanlineStride �޼ҵ带
         * ����ϵ��� �϶�.
         */
        return createIconFromRaster0(
            ( (DataBufferInt) dataBuffer).getData(), iconmask,
            raster.getWidth(), raster.getWidth(), raster.getHeight());
    }

    /**
     * �־��� �����ͷκ��� HICON�� �����Ͽ�, �� �ڵ��� �����͸� ��ȯ�Ѵ�.
     */
    private native long createIconFromRaster0(int[] data, byte[] maskData,
                                              int ss, int width, int height);

    /**
     * ������ �ڵ��� �޸𸮿��� �����Ѵ�.
     */
    public native void destroyIcon(long iconHandle);
};
