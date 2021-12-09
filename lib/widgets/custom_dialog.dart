import 'package:easy_localization/easy_localization.dart' hide TextDirection;
import 'package:flutter/material.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/res/dimens.dart';
import 'package:gate_opener/widgets/app_text_view.dart';
import 'package:gate_opener/widgets/designed_button.dart';

class CustomDialog extends StatelessWidget {
  final String title;
  final String description;
  final String? initialValue;
  final bool inputDialog;
  final Icon icon;
  final ValueChanged<String?>? onSubmitted;
  final TextInputType textInputType;
  final TextDirection? inputTextDirection;

  const CustomDialog(
      {Key? key,
      required this.title,
      required this.description,
      required this.icon,
      this.inputDialog = false,
      this.initialValue,
      this.onSubmitted,
      this.textInputType = TextInputType.text,
      this.inputTextDirection})
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
                top: Dimens.dialogPadding + icon.size! ,
                bottom: Dimens.dialogPadding,
                right: Dimens.dialogPadding),
            margin: EdgeInsets.only(top: icon.size!),
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
                  text: title,
                  style: Theme.of(context).textTheme.headline6,
                  textAlign: TextAlign.start,
                ),
                SizedBox(height: 8,),
                AppTextView(
                  text: description,
                  style: Theme.of(context).textTheme.bodyText1,
                  textAlign: TextAlign.start,
                ),
                Visibility(
                  visible: inputDialog,
                  child: TextField(
                    textAlign: TextAlign.start,
                    controller: controller,
                    style: Theme.of(context).textTheme.bodyText2,
                    keyboardType: textInputType,
                    textDirection: inputTextDirection,
                    autofocus: true,
                    onChanged: (t) {
                      text = t;
                      print("onChanged: text: $text");
                    },
                    decoration: InputDecoration(labelText: title, contentPadding: EdgeInsets.zero, focusColor: AppColors.shareButton),
                  ),
                ),
                SizedBox(
                  height: 22,
                ),
                Align(
                  alignment: Alignment.bottomRight,
                  child: Row(
                    children: [
                      DesignedButton(
                        outlined: true,
                        height: 35,
                        text: tr('approve'),
                        onPressed: () {
                          Navigator.of(context).pop();
                          if(!inputDialog){
                            onSubmitted?.call(null);
                          } else if(text?.isNotEmpty == true){
                              onSubmitted?.call(text);
                          }
                        },
                        color: AppColors.shareButton,
                      ),
                      SizedBox(width: 8,),
                      DesignedButton(
                        outlined: true,
                        height: 35,
                        text: tr('cancel'),
                        onPressed: () {
                          Navigator.of(context).pop();
                        },
                        color: AppColors.shareButton,
                      ),
                    ],
                  ),
                )
              ],
            ),
          ),
          Positioned(
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
              ))
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
