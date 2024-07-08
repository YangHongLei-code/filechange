package com.example.filechange;





import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@CrossOrigin
public class MyController {
    @RequestMapping("/")
    public String html() {
        return "/file1";
    }
    @RequestMapping("/ip")
    @ResponseBody
    public String ip() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        System.out.println("ffffff");
        return "localhost";
    }
    @RequestMapping("/filelist")
    @ResponseBody
    public List<Map<String,String>>  filelist(@RequestBody String path) {
        File root = new File(path);
        if (!root.exists()) {
            return null;
        }

        File[] files= root.listFiles();
        List<Map<String,String>> fileNames = new ArrayList<>();
        for (File file:files) {
            boolean flag=file.isDirectory();
            Map<String,String> map=new HashMap<String,String>();
            if (flag){
                map.put("path",file.getPath());
                map.put("flag",flag+"");
            }else {
                map.put("path",file.getPath());
                map.put("flag",flag+"");
            }
            fileNames.add(map);
        }
        return fileNames;
    }






    @RequestMapping("/download")
    @ResponseBody
    public void download( HttpServletResponse response,@RequestBody String fileName){
        try {
            System.out.println(fileName);
            // path是指想要下载的文件的路径
            File file = new File(fileName);
            System.out.println(file.getPath());
            // 获取文件名
            String filename = file.getName();
            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            response.setHeader("Access-Control-Allow-Origin", "*");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @RequestMapping("/upload")
    public void upload(MultipartFile file, HttpServletResponse response) throws IOException {
        String path="/app/";
        String name=file.getOriginalFilename();

        File file1=new File(path+name);
        if (file1.exists()){
            UUID uuid = UUID.randomUUID();
            name=name+uuid.toString();
        }

        try {
        byte [] bytes = file.getBytes();
        OutputStream out = new FileOutputStream(path+name);
        out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.sendRedirect("/");
    }
}
