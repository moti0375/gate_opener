import 'dart:math';
import 'package:flutter/material.dart';
import 'package:gate_opener/res/colors.dart';

class AppTextView extends StatelessWidget {
  static const double _DEFAULT_PADDING = 4;
  static const int _MAX_FLEX = 5;

  final String? text;
  final Color color;
  final TextAlign? textAlign;
  final Widget? rightIcon;
  final Widget? leftIcon;
  final double iconPadding;
  final bool symetricPadding;
  final double contentPadding;
  final int? maxLines;
  final TextOverflow? textOverflow;
  final VoidCallback? onLeftIconClicked;
  final VoidCallback? onRightIconClicked;
  final double? lettersSpacing;
  final TextStyle? style;

  const AppTextView(
      {Key? key,
        @required this.text,
        this.color = AppColors.black,
        this.textAlign,
        this.rightIcon,
        this.leftIcon,
        this.iconPadding = _DEFAULT_PADDING,
        this.contentPadding = _DEFAULT_PADDING,
        this.maxLines,
        this.textOverflow,
        this.onRightIconClicked,
        this.onLeftIconClicked,
        this.symetricPadding = true,
        this.style,
        this.lettersSpacing})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    int flex = text != null ? min(text!.split(" ").length + 2, _MAX_FLEX) : 1;

    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: <Widget>[
        if (rightIcon != null)
          Flexible(
            flex: 1,
            child: InkWell(
              onTap: onRightIconClicked,
              child: Padding(
                padding: EdgeInsets.all(iconPadding),
                child: rightIcon,
              ),
            ),
          ),
        Flexible(
          flex: flex,
          fit: FlexFit.loose,
          child: Container(
            padding: _calculatePadding(),
            child: Text(
              text ?? "",
              overflow: textOverflow,
              maxLines: maxLines,
              textAlign: textAlign,
              style: style,
            ),
          ),
        ),
        if (leftIcon != null)
          Flexible(
            flex: 1,
            child: InkWell(
              onTap: onLeftIconClicked,
              child: Padding(
                padding: EdgeInsets.all(iconPadding),
                child: leftIcon,
              ),
            ),
          )
      ],
    );
  }

  EdgeInsets _calculatePadding() {
    if (rightIcon != null && leftIcon != null) {
      //Both icons
      return EdgeInsets.symmetric(horizontal: 0);
    }

    if (rightIcon != null && leftIcon == null && symetricPadding) {
      //Right icon
      return EdgeInsets.only(left: iconPadding * 2);
    }

    if (leftIcon != null && rightIcon == null && symetricPadding) {
      //Left icon
      return EdgeInsets.only(right: iconPadding * 2);
    }

    return EdgeInsets.symmetric(horizontal: this.contentPadding); //No icons - set default content padding
  }
}