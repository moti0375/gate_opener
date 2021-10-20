import 'package:flutter/material.dart';
import 'package:gate_opener/pages/home/home_page.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/res/dimens.dart';
import 'package:google_fonts/google_fonts.dart';

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
          textTheme: TextTheme(
              headline4: GoogleFonts.workSans(color: Colors.white, fontWeight: FontWeight.bold, fontSize: Dimens.headline4),
              headline6: GoogleFonts.workSans(color: AppColors.charcoalGrey, fontWeight: FontWeight.bold, fontSize: Dimens.headline6),
              headline5: GoogleFonts.workSans(color: AppColors.charcoalGrey, fontSize: Dimens.headline4),
              button: GoogleFonts.workSans(color: Theme.of(context).buttonColor, fontSize: Dimens.button, fontWeight: FontWeight.bold),
              caption: GoogleFonts.workSans(color: AppColors.charcoalGrey, fontSize: Dimens.caption),
              bodyText1: GoogleFonts.workSans(color: AppColors.charcoalGrey, fontSize: Dimens.body1),
              bodyText2: GoogleFonts.workSans(color: AppColors.charcoalGreyOpacity80, fontSize: Dimens.body2, height: Dimens.textHeight),
              subtitle2: GoogleFonts.workSans(color: AppColors.charcoalGrey, fontSize: Dimens.subtitle, fontWeight: FontWeight.bold),
              subtitle1: GoogleFonts.workSans(color: AppColors.pureGray, fontSize: Dimens.subtitle1, height: Dimens.textHeight,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 0.5)
          ),
          primarySwatch: createMaterialColor(AppColors.primary),
          accentColor: AppColors.pureGray,
          buttonColor: AppColors.pureGray),
      home: HomePage.create(),
    );
  }
}
