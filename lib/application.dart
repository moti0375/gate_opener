import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gate_opener/pages/home/home_page.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/res/dimens.dart';
import 'package:google_fonts/google_fonts.dart';

class MyApp extends StatelessWidget {


  @override
  Widget build(BuildContext context) {
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
    ]);

    return MaterialApp(
        title: 'Flutter Demo',
        theme: ThemeData(
        inputDecorationTheme: Theme.of(context).inputDecorationTheme.copyWith(
        border: UnderlineInputBorder(
            borderSide: BorderSide(color: AppColors.shareButton)),
        focusedBorder: UnderlineInputBorder(
          borderSide: BorderSide(color: AppColors.shareButton),),
        enabledBorder: UnderlineInputBorder(
          borderSide: BorderSide(color: AppColors.shareButton),)),
        textSelectionTheme: Theme
            .of(context)
            .textSelectionTheme
            .copyWith(cursorColor: AppColors.shareButton,
            selectionColor: AppColors.shareButton),
        cardTheme: Theme
            .of(context)
            .cardTheme
            .copyWith(
            elevation: 3,
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.all(
                    Radius.circular(Dimens.cornerRadius))
            )
        ),
        textTheme: TextTheme(
            headline4: GoogleFonts.workSans(color: Colors.white,
                fontWeight: FontWeight.bold,
                fontSize: Dimens.headline4),
            headline6: GoogleFonts.workSans(color: AppColors.charcoalGrey,
                fontWeight: FontWeight.bold,
                fontSize: Dimens.headline4),
            headline5: GoogleFonts.workSans(
                color: AppColors.charcoalGrey, fontSize: Dimens.headline4),
            button: GoogleFonts.workSans(color: Theme
                .of(context)
                .buttonColor,
                fontSize: Dimens.button,
                fontWeight: FontWeight.bold),
            caption: GoogleFonts.workSans(
                color: AppColors.charcoalGrey, fontSize: Dimens.caption),
            bodyText1: GoogleFonts.workSans(
                color: AppColors.charcoalGrey, fontSize: Dimens.body1),
            bodyText2: GoogleFonts.workSans(
                color: AppColors.charcoalGreyOpacity80,
                fontSize: Dimens.body2,
                height: Dimens.textHeight),
            subtitle2: GoogleFonts.workSans(color: AppColors.charcoalGrey,
                fontSize: Dimens.subtitle,
                fontWeight: FontWeight.bold),
            subtitle1: GoogleFonts.workSans(color: AppColors.pureGray,
                fontSize: Dimens.subtitle1,
                height: Dimens.textHeight,
                fontWeight: FontWeight.bold,
                letterSpacing: 0.5)
        ),
        primarySwatch: createMaterialColor(AppColors.primary),
        accentColor: AppColors.pureGray,
        dividerColor: AppColors.GeneralDividerGray,
        buttonColor: AppColors.primary,
        iconTheme: IconThemeData(
            color: AppColors.pureGray
        )),
    home: HomePage.create(),
    );
  }
}
