import 'package:flutter/material.dart';
import 'package:gate_opener/res/dimens.dart';

class AddGateCardItem extends StatelessWidget {
  final VoidCallback onPressed;

  const AddGateCardItem({Key? key, required this.onPressed}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    double size = MediaQuery.of(context).size.width / 2;
    Color color = Theme.of(context).iconTheme.color?.withOpacity(0.8) ?? Colors.black;

    return InkWell(
      onTap: onPressed,
      child: Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
            borderRadius: BorderRadius.all(Radius.circular(Dimens.cornerRadius)),
            border: Border.all(color: color),
            color: Colors.white),
        child: Icon(Icons.add, size: size * 0.5, color: color),
      ),
    );
  }
}
