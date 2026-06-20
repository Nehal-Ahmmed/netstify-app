package com.nhbhuiyan.nestify.projectplans.Presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients

@Composable
fun HeroSectionProjectPlan() {
    val horizontalPadding = 16.dp
    val verticalPadding = 16.dp

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        //contents
        val (herobox, thinkingimage, title1, title2, profile, verticaldivider, whiteBox) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .height(255.dp)
                .background(brush = NestifyGradients.meshGradient())
                .constrainAs(herobox) {
                    top.linkTo(parent.top)
                }
        )

        Image(
            painter = painterResource(R.drawable.pplanner),
            contentDescription = null,
            modifier = Modifier
                .padding(start = horizontalPadding, end = horizontalPadding)
                .constrainAs(thinkingimage){
                    bottom.linkTo(herobox.bottom)
                    end.linkTo(verticaldivider.start)
                    start.linkTo(herobox.start)
                }
        )


        Box(
            modifier = Modifier
                .width(3.dp)
                .height(200.dp)
                .padding(vertical = verticalPadding)
                .background(colorResource(R.color.grey))
                .constrainAs(verticaldivider) {
                    top.linkTo(herobox.top)
                    bottom.linkTo(herobox.bottom)
                    start.linkTo(thinkingimage.end)
                    end.linkTo(title1.start)
                }
        )

        Text(
            text = "Take your next move",
            fontSize = 25.sp,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
                .constrainAs(title1) {
                    centerTo(herobox)
                    top.linkTo(profile.bottom)
                    start.linkTo(verticaldivider.end)

                }
        )

        Text(
            text = "Write your thinking here for your next project.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
                .constrainAs(title2) {
                    top.linkTo(title1.bottom)
                    bottom.linkTo(whiteBox.top)
                    start.linkTo(verticaldivider.start)

                }
        )

        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(110.dp)
                .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                .constrainAs(whiteBox) {
                    top.linkTo(herobox.bottom)
                    bottom.linkTo(herobox.bottom)
                }
                .clip(RoundedCornerShape(10.dp))
        ) {
            val(icon1, icon2, balance, reward, amount, wallet, arrow1, arrow2, arrow3,line1, line2) = createRefs()

            Image(
                painter = painterResource(R.drawable.wallet),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = horizontalPadding, top = verticalPadding)
                    .constrainAs(icon1) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
            )

            Text(
                text = "Total Ideas : ",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(wallet) {
                        bottom.linkTo(icon1.bottom)
                        start.linkTo(icon1.end)
                    }
            )


            Image(
                painter = painterResource(R.drawable.medal),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = horizontalPadding, bottom = verticalPadding)
                    .constrainAs(icon2) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            Text(
                text = "Completed : ",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(reward) {
                        top.linkTo(icon2.top)
                        start.linkTo(icon2.end)
                    }
            )


            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = verticalPadding)
                    .background(colorResource(R.color.grey))
                    .constrainAs(line1) {
                        centerTo(parent)
                    }
            )
            Box(
                modifier=Modifier
                    .height(1.dp)
                    .width(170.dp)
                    .padding(horizontal = 16.dp)
                    .background(colorResource(R.color.grey))
                    .constrainAs(line2) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }

            )
            Text(
                text = "Working with : ",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                color = Color.Black,
                modifier = Modifier
                    .padding(start = horizontalPadding, top = 32.dp)
                    .constrainAs(balance) {
                        top.linkTo(parent.top)
                        start.linkTo(line1.end)
                    }
            )
            Text(
                text = "Nestify",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = horizontalPadding, top = 8.dp)
                    .constrainAs(amount) {
                        top.linkTo(balance.bottom)
                        start.linkTo(balance.start)
                    }
            )

        }

    }
}