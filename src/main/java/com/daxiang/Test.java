package com.daxiang;


import com.daxiang.action.AndroidAction;
import com.daxiang.utils.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;


public class Test {

   public static void main(String[] args) throws Exception {
      AndroidAction androidAction = new AndroidAction();
      String cmd = "\nsvc help\n";
      String s = SerialTool.execSerialCmd("COM4",cmd,2000);
      System.out.println(s);
   }

}

