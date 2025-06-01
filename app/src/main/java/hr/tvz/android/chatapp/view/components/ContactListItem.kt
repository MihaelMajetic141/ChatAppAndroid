package hr.tvz.android.chatapp.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.data.dto.ContactDTO


@Composable
fun ContactListItem(
    contactDTO: ContactDTO,
    subHeader: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier
) {
    Row() {
        Box(modifier = Modifier
            .background(if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface)
        ) {
            SubcomposeAsyncImage(
                model = "${BuildConfig.SERVER_IP}/api/media/${contactDTO.imageFileId}",
                contentDescription = "Conversation image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClick)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            if (isSelected) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Selected",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .clip(CircleShape)
                        .padding(4.dp)
                )
            }
        }
        Column {
            Text(
                text = contactDTO.username,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )
            Text(
                text = subHeader,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )
        }
    }

}