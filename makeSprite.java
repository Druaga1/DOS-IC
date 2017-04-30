/**
 * Created by Nick on 1/6/2017.
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class makeSprite {

    static ArrayList<String> colors = new ArrayList<String>();
    static FileManager fm = new FileManager();
    static ConvertUtil converter = new ConvertUtil();
    static boolean debug = false;


    public static void main(String[] args){
        System.out.println("");
        boolean print_help = false;
        boolean print_version = false;

        for(String line:args){
            if(line.equals("-h"))
                print_help = true;
            if(line.equals("-d"))
                debug = true;
            if(line.equals("-v"))
                print_version = true;
        }
       if(args.length > 0 && print_help){
            System.out.println("Specify an image file (include the file extension) to be able to convert");
        }
        if(args.length > 0 && print_version){
            System.out.println("DankOS Image Converter V0.0.1 (Developed by CHAD2430)");
            System.out.println("-----------------------------------------------------");
            System.out.println("Designed to be used for converting & compressing");
            System.out.println("image files that can be then read by DankOS.");
            System.out.println("-----------------------------------------------------");
        }
        if(print_help || print_version)
            System.exit(0);
        loadColors("palette.txt");
        BufferedImage oimg = null;
        BufferedImage img2 = null;
        ArrayList<Integer> byteResult = new ArrayList();
        int width = 0;
        String file = "";
        boolean found = false;
        try {
            file = args[0];
            found = true;
            oimg = ImageIO.read(new File(file));
        }catch(Exception e){
            System.out.println("Failed to read from the image file!");
            if(found)
                System.out.println("Is it an image file?");
            else
                System.out.println("Check to see if the file exists!");
            //e.printStackTrace();
            System.exit(0);
        }
        String filename_ex = file.substring(0, file.length() - 4);
        if (oimg.getWidth() <= 320 && oimg.getHeight() <= 200) {
            try {

                DataOutputStream os = new DataOutputStream(new FileOutputStream(file.substring(0, file.length() - 4) + ".pic"));
                ByteBuffer buffer = ByteBuffer.allocate(4);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.putInt(oimg.getWidth());
                byte[] bytes = buffer.array();
                for (int i = 0; i < bytes.length - 2; i++) {
                    os.writeByte(bytes[i]);
                }
                os.writeByte(oimg.getHeight());
                if(debug) {
                    fm.openFile(file.substring(0, file.length() - 4) + "_result.txt");
                    fm.writeFile(oimg.getHeight()+", "+oimg.getWidth(), true);
                    System.out.printf("%-5s %-20s %-20s\n", "   Code", " |       Result Color       ", "|       Original Color");
                    System.out.println("------------------------------------------------------------");
                }
                width = oimg.getWidth();
                String res = "";
                BufferedImage img = oimg;
                for (int y = 0; y < img.getHeight(); y++) {
                    String result = "db     ";
                    for (int x = 0; x < img.getWidth(); x++) {
                        int RGB = img.getRGB(x, y);
                        int red = (RGB >> 16) & 255;
                        int green = (RGB >> 8) & 255;
                        int blue = (RGB) & 255;

                        int color = getColor(red, green, blue,x,y);
                        //System.out.println(color);
                        if(color > 247)
                            color = 0;
                        byteResult.add(color);
                        result += Integer.toHexString(color) + ", ";
                        if (color != 1) {
                            //int[] result_color = getColor(red,green,blue);
                            img.setRGB(x, y, new Color(getColorAttrib(color,0),getColorAttrib(color,1),getColorAttrib(color,2)).getRGB());
                        } else {
                        }
                    }
                    if(debug)
                        fm.writeFile(result);
                    //fm.writeFile(result);
                }
                if(debug)
                    ImageIO.write(img, "png", new File(file.substring(0, file.length() - 4) + "_converted.png"));
                byteResult = compressImage(byteResult);
                if(debug)
                    debugImage(byteResult,width);
                for(Integer item:byteResult){
                    String value = Integer.toHexString(item);
                    while(value.length() < 2){
                        value = "0"+value;
                    }
                    if(value.length() > 2){
                        String value1 = value.substring(0,(value.length()/2));
                        String value2 = value.substring(value.length()/2);
                        while(value1.length() < 2){
                            value1 = "0"+value1;
                        }
                        while(value2.length() < 2){
                            value2 = "0"+value2;
                        }
                        value = value1 + ","+value2;
                    }
                    try {
                        if(value.split(",").length > 1)
                            os.write(Integer.parseInt(value.split(",")[1], 16));
                        os.write(Integer.parseInt(value.split(",")[0], 16));
                    }catch(Exception e){}
                }
                os.close();
                if(!debug)
                    System.out.println("(If you want a .png output, add '-d' to the arguments) \n");
                System.out.println("Done converting image. Data written to '"+filename_ex+".pic' \n \n");
            } catch(IOException e){
                //System.out.println("Failed to read image '" + args[0] + "'");
                // System.out.println("Failed to read image 'GPU Chars/"+hex+".png'");
            }
        }else{
            System.out.println("Image too large!");
            }
    }

    private static void debugImage(ArrayList<Integer> test, int width) {
        String line = "";
        System.out.println("Image array:");
        for(int i=0; i<test.size();i++){
            String value = Integer.toHexString(test.get(i));
            while(value.length() < 2){
                value = "0"+value;
            }
            if(value.length() > 2){
                String value1 = value.substring(0,(value.length()/2));
                String value2 = value.substring(value.length()/2);
                while(value1.length() < 2){
                    value1 = "0"+value1;
                }
                while(value2.length() < 2){
                    value2 = "0"+value2;
                }
                value = value1 + ","+value2;
            }

            if(i == 0){
                line = value;
            }else{
                line += ","+value;
            }
            if(i != 0 && i % width == 0 || i + 2 > test.size()){
                System.out.println(line);
                line = "";
            }

        }
    }

    private static ArrayList<Integer> compressImage(ArrayList<Integer> image) {
        ArrayList<Integer> result = new ArrayList();

        for(int index = 0; index < image.size();){
            int item = image.get(index);
            int new_index = 1;
            while(index + new_index < image.size() && image.get(index + new_index) == item){
                new_index++;
                if(new_index > 0xFFFF)
                    break;
            }
            if(new_index > 3){
                result.add(0xFF);
                result.add(new_index);
                result.add(item);
            }else{
                for(int i=0; i<new_index; i++)
                    result.add(item);
            }
            index += new_index;
        }
        return result;
    }


    private static int getColorAttrib(int color_value, int index) {
        int result = 0;
        for(int x=0; x<colors.size(); x++){
            //System.out.println();
            int color_code = Integer.parseInt(colors.get(x).substring(colors.get(x).indexOf(":")+1,colors.get(x).length()));
            if(color_code == color_value){
                String color = colors.get(x).substring(0,colors.get(x).indexOf(":"));
                int cr = Integer.parseInt(color.substring(0, 2), 16);
                int cg = Integer.parseInt(color.substring(2, 4), 16);
                int cb = Integer.parseInt(color.substring(4, 6), 16);
                int[] color_whole = {cr,cg,cb};
                return color_whole[index];
            }
        }
        return result;
    }

    private static void loadColors(String file) {
        FileUtils.readTextFromJar(file);
        ArrayList<String> temp_colors = new ArrayList<String>();
        ArrayList<String> temp_color_values = new ArrayList<String>();
        System.out.println("Loading Color Value table from "+file);
        ArrayList<String> data = FileUtils.readTextFromJar(file);
        for(int i=0;i<data.size();i++){
            String line = data.get(i);
            if(line != null) {
                //int blue = Integer.parseInt(line.substring(0, 2), 16);
                //int green = Integer.parseInt(line.substring(3, 4), 16);
                //int red = Integer.parseInt(line.substring(4, 6), 16);
                //System.out.println(Integer.toHexString(i) + " : " + red + "," + green + "," + blue);

                //int[] color = {red, green, blue};
                temp_colors.add(Integer.toString(Integer.parseInt(line,16))+":"+i);
                temp_color_values.add(line);
                if(debug)
                    System.out.println(Integer.parseInt(line,16)+":"+i);
                //removeElement(data,index);
            }
        }
        //int index = getIndexOfMin(temp_colors);
        //for(int index = 0; index < temp_colors.size(); index++){
        while(temp_colors.size() > 0) {
            Integer min_color_index = getIndexOfMin(temp_colors);
            int index = new Integer(min_color_index);
            String min_color_whole = temp_colors.get(min_color_index);
            int color_index = Integer.parseInt(min_color_whole.substring(min_color_whole.indexOf(":")+1));
            //rrggbb
            //min_color_whole = Integer.toHexString(Integer.parseInt(min_color_whole.substring(0,min_color_whole.indexOf(":")))) + color_index);
            //while(min_color_whole.substring(0,min_color_whole.indexOf(":")).length() < 6){
            //    min_color_whole = min_color_whole.substring(0,min_color_whole.indexOf(":")) + "0" + min_color_whole.substring(min_color_whole.indexOf(":"));
            //}
            min_color_whole = temp_color_values.get(color_index) + ":" + color_index;
            //System.out.println("Minimum Color: " + min_color_whole);
            colors.add(min_color_whole);
            temp_colors.remove(index);
            int cr = Integer.parseInt(temp_color_values.get(color_index).substring(0, 2), 16);
            int cg = Integer.parseInt(temp_color_values.get(color_index).substring(2, 4), 16);
            int cb = Integer.parseInt(temp_color_values.get(color_index).substring(4, 6), 16);
            //System.out.println(temp_colors.size());
            if(debug)
                System.out.println(min_color_whole+"  =  "+cr+","+cg+","+cb);
        }


        System.out.println("Done. \n");
       // System.exit(0);
    }

    public static void removeElement(Object[] a, int del) {
        System.arraycopy(a,del+1,a,del,a.length-1-del);
    }

    public static int getIndexOfMin(ArrayList<String> data) {
        int min = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < data.size(); i++) {
            Integer f = Integer.parseInt(data.get(i).substring(0,data.get(i).indexOf(":")));
            if (Integer.compare(f, min) < 0) {
                min = f.intValue();
                index = i;
            }
        }
        return index;
    }


    /*
    When given an RGB tuple, it will run through the palette list and choose the most appropriate color.
     */
    public static int getColor(int red, int green, int blue, int x,int y) {


        int or = Integer.parseInt(Integer.toHexString(red), 16);
        int og = Integer.parseInt(Integer.toHexString(green), 16);
        int ob = Integer.parseInt(Integer.toHexString(blue), 16);

        //int result = (((or * 7 / 255) << 5) + ((og * 7 / 255) << 2) + (ob * 3 / 255));
         //int result = (red & 0xE0) | ((green & 0xE0) >> 3) | (blue >> 6);
        //System.out.println(Integer.toBinaryString(result)+"  =  "+(256-result));
        int result = 0;
        int[] previous = {0,0,0};

        for(String color_whole:colors){
            String color    = color_whole.substring(0,color_whole.indexOf(":"));
            int color_value = Integer.parseInt(color_whole.substring(color_whole.indexOf(":")+1));

            int cr = Integer.parseInt(color.substring(0, 2), 16);
            int cg = Integer.parseInt(color.substring(2, 4), 16);
            int cb = Integer.parseInt(color.substring(4, 6), 16);
         //   if(debug) {
                //System.out.println(red + "," + green + "," + blue + "    |    " + cr + "," + cg + "," + cb + "    |     " + color + ":" + color_value);
           // }
            if((cb >= blue && cg >= green && cr >= red)){
                break;
            }
            if(cb <= blue && (cg <= green && cr <= red)) {
                result = color_value;
                previous[0] = cr;
                previous[1] = cg;
                previous[2] = cb;
                //System.out.printf("%-5s %-20s %-20s\n", result, " |       "+previous[0]+","+previous[1]+","+previous[2], "        |        "+or+","+og+","+ob);
            }
        }
        return result;
    }

}

