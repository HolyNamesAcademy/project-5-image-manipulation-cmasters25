import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Static utility class that is responsible for transforming the images.
 * Each function (or at least most functions) take in an Image and return
 * a transformed image.
 */
public class ImageManipulator {
    /**
     * Loads the image at the given path
     * @param path path to image to load
     * @return an Img object that has the given image loaded
     * @throws IOException
     */
    public static Img LoadImage(String path) throws IOException {
        return new Img(path);
    }

    /**
     * Saves the image to the given file location
     * @param image image to save
     * @param path location in file system to save the image
     * @throws IOException
     */
    public static void SaveImage(Img image, String path) throws IOException {
        image.Save(path.substring(path.length()-3), path);
    }

    /**
     * Converts the given image to grayscale (black, white, and gray). This is done
     * by finding the average of the RGB channel values of each pixel and setting
     * each channel to the average value.
     * @param image image to transform
     * @return the image transformed to grayscale
     */
    public static Img ConvertToGrayScale(Img image) {
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                RGB r = image.GetRGB(i, j);
                int c = (r.GetBlue() + r.GetRed() + r.GetGreen())/3 ;
                r.SetBlue(c);
                r.SetRed(c);
                r.SetGreen(c);
                image.SetRGB(i, j, r);
            }
        }
        return image;
    }

    /**
     * Inverts the image. To invert the image, for each channel of each pixel, we get
     * its new value by subtracting its current value from 255. (r = 255 - r)
     * @param image image to transform
     * @return image transformed to inverted image
     */
    public static Img InvertImage(Img image) {
        for(int width = 0; width<image.GetWidth(); width++){
            for(int height = 0; height<image.GetHeight(); height++){
                RGB r = image.GetRGB(width, height);
                r.SetBlue(255-r.GetBlue());
                r.SetRed(255-r.GetRed());
                r.SetGreen(255-r.GetGreen());
                image.SetRGB(width, height, r);
            }
        }
        return image;
    }

    /**
     * Converts the image to sepia. To do so, for each pixel, we use the following equations
     * to get the new channel values:
     * r = .393r + .769g + .189b
     * g = .349r + .686g + .168b
     * b = 272r + .534g + .131b
     * @param image image to transform
     * @return image transformed to sepia
     */
    public static Img ConvertToSepia(Img image) {
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                RGB r = image.GetRGB(i, j);
                int blue = (int)((r.GetRed()*0.272) + (r.GetGreen()*0.534) + (r.GetBlue()*0.131));
                int red = (int)((r.GetRed()*0.393) + (r.GetGreen()*0.769) + (r.GetBlue()*0.189));
                int green = (int)((r.GetRed()*0.349) + (r.GetGreen()*0.686) + (r.GetBlue()*0.168));
                r.SetBlue(blue);
                r.SetRed(red);
                r.SetGreen(green);
                image.SetRGB(i, j, r);
            }
        }
        return image;
    }

    /**
     * Creates a stylized Black/White image (no gray) from the given image. To do so:
     * 1) calculate the luminance for each pixel. Luminance = (.299 r^2 + .587 g^2 + .114 b^2)^(1/2)
     * 2) find the median luminance
     * 3) each pixel that has luminance >= median_luminance will be white changed to white and each pixel
     *      that has luminance < median_luminance will be changed to black
     * @param image image to transform
     * @return black/white stylized form of image
     */
    public static Img ConvertToBW(Img image) {
        ArrayList<Double> list = new ArrayList<Double>();
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                int red = image.GetRGB(i, j).GetRed();
                int green = image.GetRGB(i, j).GetGreen();
                int blue = image.GetRGB(i, j).GetBlue();
                double luminance = ((0.299)*(red*red)) + ((0.587)*(green*green)) + ((0.114)*(blue*blue));
                list.add(luminance);
            }
        }

        Collections.sort(list);

        double medLum = 0;
        if(list.size()%2==1){
            medLum = list.get((int)(list.size()/2 + 0.5));
        }
        else
            medLum = (list.get(list.size()/2) + list.get(list.size()/2 +1))/2;

        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                int red = image.GetRGB(i, j).GetRed();
                int green = image.GetRGB(i, j).GetGreen();
                int blue = image.GetRGB(i, j).GetBlue();
                double tempLum = ((0.299)*(red*red)) + ((0.587)*(green*green)) + ((0.114)*(blue*blue));
                if(tempLum>medLum || tempLum == medLum){
                    RGB t = new RGB(255, 255, 255);
                    image.SetRGB(i, j, t);
                }
                else{
                    RGB t = new RGB(0, 0, 0);
                    image.SetRGB(i, j, t);
                }
            }
        }

        return image;
    }

    /**
     * Rotates the image 90 degrees clockwise.
     * @param image image to transform
     * @return image rotated 90 degrees clockwise
     */
    public  static Img RotateImage(Img image) {
        Img newImage = new Img(image.GetHeight(), image.GetWidth());
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                RGB r = image.GetRGB(i, j);
                newImage.SetRGB(newImage.GetWidth()-j-1, i, r);
            }
        }
        return newImage;
    }

    /**
     * Applies an Instagram-like filter to the image. To do so, we apply the following transformations:
     * 1) We apply a "warm" filter. We can produce warm colors by reducing the amount of blue in the image
     *      and increasing the amount of red. For each pixel, apply the following transformation:
     *          r = r * 1.2
     *          g = g
     *          b = b / 1.5
     * 2) We add a vignette (a black gradient around the border) by combining our image with
     *      an image of a halo (you can see the image at resources/halo.png). We take 65% of our
     *      image and 35% of the halo image. For example:
     *          r = .65 * r_image + .35 * r_halo
     * 3) We add decorative grain by combining our image with a decorative grain image
     *      (resources/decorative_grain.png). We will do this at a .95 / .5 ratio.
     * @param image image to transform
     * @return image with a filter
     * @throws IOException
     */
    public static Img InstagramFilter(Img image) throws IOException {
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                RGB rgb = image.GetRGB(i, j);
                int red = (int)(rgb.GetRed() *1.2);
                int green = rgb.GetGreen();
                int blue = (int)(rgb.GetBlue()/1.5);
                RGB newRGB = new RGB(red, green, blue);
                image.SetRGB(i, j, newRGB);
            }
        }
        Img halo = new Img("resources/halo.png");
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                RGB rgbImage = image.GetRGB(i, j);
                RGB rgbHalo = halo.GetRGB(i, j);
                int red = (int)(rgbImage.GetRed()*0.65 + rgbHalo.GetRed()*0.35);
                int green = (int)(rgbImage.GetGreen()*0.65 + rgbHalo.GetGreen()*0.35);
                int blue = (int)(rgbImage.GetBlue()*0.65 + rgbHalo.GetBlue()*0.35);
                RGB newRGB = new RGB(red, green, blue);
                image.SetRGB(i, j, newRGB);
            }
        }
        Img grain = new Img("resources/decorative_grain.png");
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight();j++){
                RGB rgbImage = image.GetRGB(i, j);
                RGB rgbGrain = grain.GetRGB(i, j);
                int red = (int)(rgbImage.GetRed()*0.95 + rgbGrain.GetRed()*0.05);
                int green = (int)(rgbImage.GetGreen()*0.95 + rgbGrain.GetGreen()*0.05);
                int blue = (int)(rgbImage.GetBlue()*0.95 + rgbGrain.GetBlue()*0.05);
                RGB newRGB = new RGB(red, green, blue);
                image.SetRGB(i, j, newRGB);
            }
        }
        return image;
    }

    /**
     * Sets the given hue to each pixel image. Hue can range from 0 to 360. We do this
     * by converting each RGB pixel to an HSL pixel, Setting the new hue, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param hue amount of hue to add
     * @return image with added hue
     */
    public static Img SetHue(Img image, int hue) {
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                HSL h = image.GetRGB(i, j).ConvertToHSL();
                h.SetHue(hue);
                RGB r = h.GetRGB();
                image.SetRGB(i, j, r);
            }
        }
        return image;
    }

    /**
     * Sets the given saturation to the image. Saturation can range from 0 to 1. We do this
     * by converting each RGB pixel to an HSL pixel, setting the new saturation, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param saturation amount of saturation to add
     * @return image with added hue
     */
    public static Img SetSaturation(Img image, double saturation) {
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                HSL h = image.GetRGB(i, j).ConvertToHSL();
                h.SetSaturation(saturation);
                RGB r = h.GetRGB();
                image.SetRGB(i, j, r);
            }
        }
        return image;
    }

    /**
     * Sets the lightness to the image. Lightness can range from 0 to 1. We do this
     * by converting each RGB pixel to an HSL pixel, setting the new lightness, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param lightness amount of hue to add
     * @return image with added hue
     */
    public static Img SetLightness(Img image, double lightness) {
        for(int i = 0; i<image.GetWidth(); i++){
            for(int j = 0; j<image.GetHeight(); j++){
                HSL h = image.GetRGB(i, j).ConvertToHSL();
                h.SetLightness(lightness);
                RGB r = h.GetRGB();
                image.SetRGB(i, j, r);
            }
        }
        return image;
    }
}
