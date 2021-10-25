import 'package:flutter/material.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/res/dimens.dart';
import 'package:gate_opener/widgets/app_text_view.dart';
import 'package:gate_opener/widgets/designed_button.dart';

class CustomDialog extends StatelessWidget {
  final String title;
  final String description;
  final String? initialValue;
  final Widget? icon;
  final ValueChanged<String?>? onSubmitted;
  final TextInputType textInputType;

  const CustomDialog(
      {Key? key,
      required this.title,
      required this.description,
      required this.icon,
      this.initialValue,
      this.onSubmitted,
      this.textInputType = TextInputType.text})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String? text;

    TextEditingController controller =
        TextEditingController(text: initialValue);
    controller.selection = TextSelection(baseOffset: 0, extentOffset: initialValue != null ? initialValue!.length : 0);

    Widget _contentBox(BuildContext context) {
      return Stack(
        children: [
          Container(
            padding: EdgeInsets.only(
                left: Dimens.dialogPadding,
                top: Dimens.dialogPadding + Dimens.avatarRadius,
                bottom: Dimens.dialogPadding,
                right: Dimens.dialogPadding),
            margin: EdgeInsets.only(top: Dimens.avatarRadius),
            decoration: BoxDecoration(
                shape: BoxShape.rectangle,
                color: Theme.of(context).backgroundColor,
                borderRadius:
                    BorderRadius.all(Radius.circular(Dimens.cornerRadius)),
                boxShadow: [
                  BoxShadow(
                      color: Colors.black,
                      offset: Offset(0, 10),
                      blurRadius: 10),
                ]),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                AppTextView(
                  text: description,
                  maxLines: 1,
                  style: Theme.of(context).textTheme.bodyText1,
                  textAlign: TextAlign.center,
                ),
                TextField(
                  textAlign: TextAlign.start,
                  controller: controller,
                  style: Theme.of(context).textTheme.bodyText2,
                  keyboardType: textInputType,
                  autofocus: true,
                  onChanged: (t) {
                    text = t;
                    print("onChanged: text: $text");
                  },
                  decoration: InputDecoration(labelText: title, contentPadding: EdgeInsets.zero, focusColor: AppColors.shareButton),
                ),
                SizedBox(
                  height: 22,
                ),
                Align(
                  alignment: Alignment.bottomRight,
                  child: DesignedButton(
                    outlined: true,
                    height: 35,
                    text: "OK",
                    onPressed: () {
                      Navigator.of(context).pop();
                      if(text?.isNotEmpty == true){
                        onSubmitted?.call(text);
                      }
                    },
                    color: AppColors.shareButton,
                  ),
                )
              ],
            ),
          ),
          Visibility(
            visible: icon != null,
            child: Positioned(
                right: Dimens.dialogPadding,
                left: Dimens.dialogPadding,
                child: CircleAvatar(
                  backgroundColor: AppColors.GeneralDividerGray,
                  radius: Dimens.avatarRadius,
                  child: ClipRRect(
                    borderRadius:
                        BorderRadius.all(Radius.circular(Dimens.avatarRadius)),
                    child: icon,
                  ),
                )),
          )
        ],
      );
    }

    return Dialog(
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.all(Radius.circular(Dimens.cornerRadius))),
      elevation: 0,
      backgroundColor: Colors.transparent,
      child: _contentBox(context),
    );
  }
}
