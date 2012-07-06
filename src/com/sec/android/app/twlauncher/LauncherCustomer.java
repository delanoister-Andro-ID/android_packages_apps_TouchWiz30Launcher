/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sec.android.app.twlauncher;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class LauncherCustomer
{

    private LauncherCustomer()
    {
        loadXMLFile("customer.xml", 0);
    }

    public static LauncherCustomer getInstance(String s, int i)
    {
        if(s == null)
            sInstance.loadXMLFile("customer.xml", i);
        else
            sInstance.loadXMLFile(s, i);
        return sInstance;
    }

    private int getTagCount(NodeList nodelist)
    {
        int i = 0;
        if(nodelist != null)
            i = nodelist.getLength();
        return i;
    }

    private NodeList getTagList(Node node, String s)
    {
        NodeList nodelist;
        if(mDoc == null || node == null)
        {
            nodelist = null;
        } else
        {
            Element element = mDoc.createElement(node.getNodeName());
            NodeList nodelist1 = node.getChildNodes();
            if(nodelist1 != null)
            {
                int i = nodelist1.getLength();
                for(int j = 0; j < i; j++)
                {
                    Node node1 = nodelist1.item(j);
                    if(node1.getNodeName().equals(s))
                        element.appendChild(node1);
                }

            }
            nodelist = element.getChildNodes();
        }
        return nodelist;
    }

    private Node getTagNode(String s)
    {
        if(mRoot != null) goto _L2; else goto _L1
_L1:
        Node node1 = null;
_L4:
        return node1;
_L2:
        Node node = mRoot;
        StringTokenizer stringtokenizer = new StringTokenizer(s, ".");
        do
        {
            if(!stringtokenizer.hasMoreTokens())
                break;
            String s1 = stringtokenizer.nextToken();
            if(node == null)
            {
                node1 = null;
                continue; /* Loop/switch isn't completed */
            }
            node = getTagNode(node, s1);
        } while(true);
        node1 = node;
        if(true) goto _L4; else goto _L3
_L3:
    }

    private Node getTagNode(Node node, String s)
    {
        NodeList nodelist;
        int i;
        int j;
        nodelist = node.getChildNodes();
        if(nodelist == null)
            break MISSING_BLOCK_LABEL_66;
        i = nodelist.getLength();
        j = 0;
_L3:
        Node node2;
        if(j >= i)
            break MISSING_BLOCK_LABEL_66;
        node2 = nodelist.item(j);
        if(!s.equals(node2.getNodeName())) goto _L2; else goto _L1
_L1:
        Node node1 = node2;
_L4:
        return node1;
_L2:
        j++;
          goto _L3
        node1 = null;
          goto _L4
    }

    private String getTagValue(Node node)
    {
        String s;
        if(node == null)
            s = null;
        else
            s = node.getFirstChild().getNodeValue();
        return s;
    }

    private void initLauncherCustomer()
    {
        mCustomerNode = null;
        mCustomerList = null;
        mCustomerCount = 0;
    }

    private void loadXMLFile(String s, int i)
    {
        initLauncherCustomer();
        mDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File((new StringBuilder()).append("/system/csc/").append(s).toString()));
        mRoot = mDoc.getDocumentElement();
        setLauncherCustomer(i);
_L1:
        return;
        ParserConfigurationException parserconfigurationexception;
        parserconfigurationexception;
        Log.e("Launcher.LauncherCustomer", (new StringBuilder()).append("ParserConfigurationException:").append(parserconfigurationexception).toString());
          goto _L1
        SAXException saxexception;
        saxexception;
        Log.e("Launcher.LauncherCustomer", (new StringBuilder()).append("SAXException: ").append(saxexception).toString());
          goto _L1
        IOException ioexception;
        ioexception;
        Log.e("Launcher.LauncherCustomer", (new StringBuilder()).append("IOException: ").append(ioexception).toString());
          goto _L1
    }

    private void setLauncherCustomer(int i)
    {
        if(i != 0) goto _L2; else goto _L1
_L1:
        mCustomerNode = getTagNode("Settings.Main.Display");
        mCustomerList = getTagList(mCustomerNode, "Wallpaper");
        mCustomerCount = getTagCount(mCustomerList);
_L4:
        return;
_L2:
        if(i == 1)
        {
            mCustomerNode = getTagNode("Launcher");
            mCustomerList = getTagList(mCustomerNode, "favorites");
            mCustomerCount = getTagCount(mCustomerList);
        } else
        if(i == 2)
        {
            mCustomerNode = getTagNode("Launcher");
            mCustomerList = getTagList(mCustomerNode, "mainapps");
            mCustomerCount = getTagCount(mCustomerList);
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    public int getCustomerCount()
    {
        return mCustomerCount;
    }

    public void getCustomerFavoriteInfo(int i, String as[])
    {
        Node node = mCustomerList.item(i);
        if(node != null)
        {
            as[0] = getTagValue(getTagNode(node, "favorite_type"));
            as[1] = getTagValue(getTagNode(node, "packageName"));
            as[2] = getTagValue(getTagNode(node, "className"));
            as[3] = getTagValue(getTagNode(node, "screen"));
            as[4] = getTagValue(getTagNode(node, "launcher_x"));
            as[5] = getTagValue(getTagNode(node, "launcher_y"));
            as[6] = getTagValue(getTagNode(node, "launcher_spanX"));
            as[7] = getTagValue(getTagNode(node, "launcher_spanY"));
            as[8] = getTagValue(getTagNode(node, "folderId"));
            as[9] = getTagValue(getTagNode(node, "folderName"));
        }
    }

    public void getCustomerMainAppInfo(int i, String as[])
    {
        Node node = mCustomerList.item(i);
        if(node != null)
        {
            as[0] = getTagValue(getTagNode(node, "packageName"));
            as[1] = getTagValue(getTagNode(node, "className"));
        }
    }

    private static int mCustomerCount;
    private static NodeList mCustomerList;
    private static Node mCustomerNode;
    private static Document mDoc;
    private static Node mRoot;
    private static LauncherCustomer sInstance = new LauncherCustomer();

}
