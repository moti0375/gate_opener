import 'package:flutter/material.dart';
import 'package:gate_opener/application.dart';

import 'di/locator.dart';

void main() async {
  await setupLocator();
  runApp(MyApp());
}
