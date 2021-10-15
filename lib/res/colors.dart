import 'dart:ui';

import 'package:flutter/material.dart';

abstract class AppColors{
  static const Color primary = Color(0xff5675fb);
  static const Color primaryOn = Color(0xffFFFFFF);
  static const Color buttonsPrimary = Color.fromARGB(255, 38, 78, 240);
  static const Color shareButton = Color.fromARGB(255, 109, 204, 155);
  static const Color charcoalGrey = Color(0xff33333d);
  static const Color charcoalGreyOpacity80 = Color(0xcc33333d);
  static const Color pureGray = Colors.grey;
  static const Color white = Colors.white;
  static const Color black = Colors.black;
}


MaterialColor createMaterialColor(Color color) {
  List strengths = <double>[.05];
  final swatch = <int, Color>{};
  final int r = color.red, g = color.green, b = color.blue;

  for (int i = 1; i < 10; i++) {
    strengths.add(0.1 * i);
  }
  strengths.forEach((strength) {
    final double ds = 0.5 - strength;
    swatch[(strength * 1000).round()] = Color.fromRGBO(
      r + ((ds < 0 ? r : (255 - r)) * ds).round(),
      g + ((ds < 0 ? g : (255 - g)) * ds).round(),
      b + ((ds < 0 ? b : (255 - b)) * ds).round(),
      1,
    );
  });
  return MaterialColor(color.value, swatch);
}