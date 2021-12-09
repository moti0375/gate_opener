import 'package:easy_localization/easy_localization.dart' hide TextDirection;
import 'package:flutter/material.dart';
import 'package:gate_opener/widgets/app_text_view.dart';

class GateCardItem extends StatelessWidget {
  final VoidCallback onPressed;
  final VoidCallback onLongPressed;

  final String title;
  final String subtitle;

  const GateCardItem(
      {Key? key, required this.title, required this.subtitle, required this.onPressed, required this.onLongPressed})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    double size = MediaQuery.of(context).size.width / 2;
    Color color = Theme.of(context).iconTheme.color?.withOpacity(0.8) ?? Colors.black;

    Widget _buildCardFooter(BuildContext context) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 8),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                Expanded(
                    child: AppTextView(
                  text: title,
                  style: Theme.of(context).textTheme.bodyText1,
                  textAlign: TextAlign.start,
                  contentPadding: 0,
                  maxLines: 1,
                )),
              ],
            ),
            Row(
              children: [
                Expanded(
                  child: AppTextView(
                    text: subtitle,
                    style: Theme.of(context).textTheme.bodyText1,
                    textAlign: TextAlign.start,
                    contentPadding: 0,
                    textDirection: TextDirection.ltr,
                    maxLines: 1,
                  ),
                ),
              ],
            )
          ],
        ),
      );
    }

    return InkWell(
      onTap: onPressed,
      onLongPress: onLongPressed,
      child: Card(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.map, size: size * 0.5, color: color),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8),
              child: Divider(thickness: 4),
            ),
            _buildCardFooter(context)
          ],
        ),
      ),
    );
  }
}
