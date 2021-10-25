import 'package:flutter/material.dart';
import 'package:gate_opener/res/strings.dart';

class DesignedButton extends StatelessWidget {
  static const double _defaultHeight = 50.0;

  final VoidCallback? onPressed;
  final String text;
  final double height;
  final double? cornerRadius;
  final Color? color;
  final bool outlined;
  final bool visible;
  DesignedButton(
      {Key? key,
      this.onPressed,
      this.text = EMPTY_STRING,
      this.height = _defaultHeight,
      this.cornerRadius,
      this.color,
      this.outlined = false,
      this.visible = true})
      : super(key: key);

  Color _getPrimaryColor(BuildContext context) {
    return this.color != null ? this.color! : Theme.of(context).primaryColor;
  }

  double _calculateCornerRadius() {
    return cornerRadius != null ? cornerRadius! : height * 50 / 100;
  }

  ButtonStyle _createButtonStyle(BuildContext context) {
    return ButtonStyle(
        padding: MaterialStateProperty.all<EdgeInsets>(
            EdgeInsets.zero),
        backgroundColor: MaterialStateProperty.resolveWith<Color>(
            (Set<MaterialState> states) {
          var disabledColor = outlined
              ? Colors.transparent
              : _getPrimaryColor(context).withOpacity(0.50);
          if (states.contains(MaterialState.disabled)) return disabledColor;
          return outlined
              ? Colors.transparent
              : _getPrimaryColor(context); // Defer to the widget's default.
        }),
        foregroundColor: MaterialStateProperty.resolveWith<Color>(
            (Set<MaterialState> states) {
          var disabledColor = (outlined
                  ? _getPrimaryColor(context)
                  : Theme.of(context).buttonColor)
              .withOpacity(0.6);
          if (states.contains(MaterialState.disabled)) return disabledColor;
          return outlined
              ? _getPrimaryColor(context)
              : Theme.of(context).buttonColor;
        }),
        side: outlined
            ? MaterialStateProperty.resolveWith<BorderSide>(
                (Set<MaterialState> states) {
                Color color = states.contains(MaterialState.disabled)
                    ? _getPrimaryColor(context).withOpacity(0.60)
                    : _getPrimaryColor(context);
                return BorderSide(color: color, width: 2);
              })
            : null,
        shape: MaterialStateProperty.all<OutlinedBorder?>(
          RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(_calculateCornerRadius()),
          ),
        ));
  }

  @override
  Widget build(BuildContext context) {
    return Visibility(
      visible: visible,
      child: Container(
        height: height,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.all(
            Radius.circular(_calculateCornerRadius()),
          ),
        ),
        child: TextButton(
            onPressed: onPressed,
            child: Text(
              text,
              maxLines: 1,
              textAlign: TextAlign.center,
            ),
            style: _createButtonStyle(context)),
      ),
    );
  }
}
