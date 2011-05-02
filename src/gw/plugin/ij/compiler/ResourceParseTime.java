package gw.plugin.ij.compiler;

import java.io.PrintWriter;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
class ResourceParseTime implements Comparable<ResourceParseTime> {
    String name;
    double parseTime;

    public ResourceParseTime(String name, double parseTime) {
      this.name = name;
      this.parseTime = parseTime;
    }

    public void print(PrintWriter writer) {
      writer.printf("%.3f", parseTime);
      writer.print(" - ");
      writer.println(name);
    }

    @Override
    public int compareTo(ResourceParseTime o) {
      if (parseTime < o.parseTime) {
        return +1;
      } else {
        return -1;
      }
    }
  }
