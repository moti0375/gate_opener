import 'package:equatable/equatable.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:gate_opener/utils/string_utils.dart';
import 'package:gate_opener/widgets/app_text_view.dart';

class ListItemViewModel extends Equatable {
  final String topRowTitle;
  final String bottomRowLeftText;
  final String bottomRowRightText;
  final String indicatorText;
  final VoidCallback? onPressed;
  final Widget? leadingIcon;

  ListItemViewModel(this.topRowTitle, this.onPressed, this.leadingIcon,
      {this.bottomRowLeftText = StringUtils.EMPTY_STRING,
      this.bottomRowRightText = StringUtils.EMPTY_STRING,
      this.indicatorText = StringUtils.EMPTY_STRING});

  @override
  List<Object> get props =>
      [topRowTitle, bottomRowLeftText, bottomRowRightText, indicatorText];

  @override
  bool get stringify => true;
}

class ListItem extends StatelessWidget {
  final ListItemViewModel? itemViewModel;
  final double padding;

  const ListItem(Key? key, {this.itemViewModel, this.padding = 16})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 75,
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        mainAxisSize: MainAxisSize.max,
        children: <Widget>[
          if (itemViewModel?.leadingIcon != null)
            Container(
              padding: EdgeInsets.symmetric(horizontal: padding),
              child: Center(
                child: itemViewModel?.leadingIcon,
              ),
            ),
          Expanded(
            child: Container(
              padding: EdgeInsets.only(right: padding),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.stretch,
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 2),
                    child: _buildTopRow(context),
                  ),
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 2),
                    child: _buildBottomRow(context),
                  )
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBottomRow(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      mainAxisSize: MainAxisSize.max,
      children: <Widget>[
        Expanded(
          child: AppTextView(
            maxLines: 1,
            text: itemViewModel?.bottomRowRightText,
            textAlign: TextAlign.start,
            style: Theme.of(context).textTheme.bodyText2,
          ),
        ),
        Expanded(
          child: Container(
            alignment: Alignment.centerLeft,
            child: AppTextView(
              maxLines: 1,
              text: itemViewModel?.bottomRowLeftText,
              textAlign: TextAlign.end,
              style: Theme.of(context).textTheme.bodyText2,
            ),
          ),
        )
      ],
    );
  }

  Widget _buildTopRow(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      mainAxisAlignment: MainAxisAlignment.end,
      children: <Widget>[
        Expanded(
          child: Container(
            child: AppTextView(
              style: Theme.of(context).textTheme.headline6,
              text: itemViewModel?.topRowTitle,
              textOverflow: TextOverflow.ellipsis,
              textAlign: TextAlign.end,
            ),
          ),
        ),
      ],
    );
  }
}
