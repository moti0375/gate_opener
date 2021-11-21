import 'package:flutter/material.dart';
import 'package:gate_opener/application.dart';
import 'di/locator.dart';
import 'package:easy_localization/easy_localization.dart';
import 'generated/codegen_loader.g.dart';

void main() async {
  await setupLocator();
  WidgetsFlutterBinding.ensureInitialized();
  await EasyLocalization.ensureInitialized();
  runApp(EasyLocalization(
      path: 'resources/langs',
      supportedLocales: [Locale('en', 'US'), Locale('he', 'IL')],
      fallbackLocale: Locale('en', 'US'),
      assetLoader: CodegenLoader(),
      child: MyApp()));
}
