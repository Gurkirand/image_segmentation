package ui;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import java.awt.Graphics;

public class ImageLabel extends javax.swing.JLabel {
	public Image _myimage;
	public int _imageW, _imageH;

	public ImageLabel(String text){
		super(text);
	}

	public void setIcon(BufferedImage img)
	{
		_imageW = img.getWidth();
		_imageH = img.getHeight();
		setIcon(new ImageIcon(img));
	}

	public Dimension getImageSize()
	{
		return new Dimension(_imageW, _imageH);
	}

	public Dimension getAdjustedImageSize()
	{
		int w, h;
		if (_imageW > _imageH)
		{
			w = this.getWidth();
			h = (int) (((1.0 * _imageH) / _imageW) * w);
		}
		else
		{
			h = this.getHeight();
			w = (int) (((1.0 * _imageW) / _imageH) * h);
		}
		return new Dimension(w, h);
	}

	public void setIcon(javax.swing.Icon icon) {
		super.setIcon(icon);
		if (icon instanceof ImageIcon)
		{
			_myimage = ((ImageIcon) icon).getImage();
		}
	}

	@Override
	public void paint(Graphics g){
		Dimension s = getAdjustedImageSize();
		int w = s.width,
		    h = s.height;

		g.drawImage(_myimage, 0, 0, w, h, null);
	}
}
