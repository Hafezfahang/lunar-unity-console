//
//  StringUtilsTest.cs
//
//  Lunar Unity Mobile Console
//  https://github.com/SpaceMadness/lunar-unity-console
//
//  Copyright 2015-2021 Alex Lementuev, SpaceMadness.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//


﻿using System.Collections.Generic;

using UnityEngine;
using UnityEditor;

using NUnit.Framework;

using LunarConsolePluginInternal;

public class StringUtilsTest
{
    #region Parsing

    [Test]
    public void TestParseFloat()
    {
        Assert.AreEqual(123f, StringUtils.ParseFloat("123"));
        Assert.AreEqual(-123f, StringUtils.ParseFloat("-123"));
        Assert.AreEqual(0f, StringUtils.ParseFloat("0", float.NaN));
        Assert.AreEqual(3.14f, StringUtils.ParseFloat("3.14"));
        Assert.AreEqual(-3.14f, StringUtils.ParseFloat("-3.14"));
        Assert.AreEqual(float.NaN, StringUtils.ParseFloat("3.14f", float.NaN));
        Assert.AreEqual(float.NaN, StringUtils.ParseFloat("abs", float.NaN));

        float actual;
        Assert.IsTrue(StringUtils.ParseFloat("123", out actual));
        Assert.AreEqual(123f, actual);

        Assert.IsTrue(StringUtils.ParseFloat("-123", out actual));
        Assert.AreEqual(-123f, actual);

        Assert.IsTrue(StringUtils.ParseFloat("0", out actual));
        Assert.AreEqual(0f, actual);

        Assert.IsTrue(StringUtils.ParseFloat("3.14", out actual));
        Assert.AreEqual(3.14f, actual);

        Assert.IsTrue(StringUtils.ParseFloat("-3.14", out actual));
        Assert.AreEqual(-3.14f, actual);

        Assert.IsFalse(StringUtils.ParseFloat("-3.14f", out _));
        Assert.IsFalse(StringUtils.ParseFloat("abs", out _));
    }

    #endregion

    #region String Representation

    [Test]
    public void TestFloatString()
    {
        Assert.AreEqual("3.14", StringUtils.ToString(3.14f));
        Assert.AreEqual("-3.14", StringUtils.ToString(-3.14f));
        Assert.AreEqual("0", StringUtils.ToString(0.0f));
        Assert.AreEqual("123", StringUtils.ToString(123.0f));
        Assert.AreEqual("-123", StringUtils.ToString(-123.0f));
    }

    #endregion

    #region Serialization

    [Test]
    public void TestStringDeserialization()
    {
        string data = "key3:value with\\nlinebreak\nkey1:value\nkey6:\nkey4:value with \"quotes\"\nkey2:value with whitespace\nkey5:value with: separator";
        IDictionary<string, string> dictionary = StringUtils.DeserializeString(data);
        Assert.AreEqual(6, dictionary.Count);
        Assert.AreEqual("value", dictionary["key1"]);
        Assert.AreEqual("value with whitespace", dictionary["key2"]);
        Assert.AreEqual("value with\nlinebreak", dictionary["key3"]);
        Assert.AreEqual("value with \"quotes\"", dictionary["key4"]);
        Assert.AreEqual("value with: separator", dictionary["key5"]);
        Assert.AreEqual("", dictionary["key6"]);
    }

    #endregion

    #region Display name

    [Test]
    public void TestDisplayName1()
    {
        Assert.IsNull(StringUtils.ToDisplayName(null));
    }

    [Test]
    public void TestDisplayName2()
    {
        Assert.AreEqual("", StringUtils.ToDisplayName(""));
    }

    [Test]
    public void TestDisplayName3()
    {
        Assert.AreEqual("Name", StringUtils.ToDisplayName("name"));
    }

    [Test]
    public void TestDisplayName4()
    {
        Assert.AreEqual("Pretty Display Name", StringUtils.ToDisplayName("prettyDisplayName"));
    }

    [Test]
    public void TestDisplayName5()
    {
        Assert.AreEqual("Display 12", StringUtils.ToDisplayName("display12"));
    }

    #endregion
}
