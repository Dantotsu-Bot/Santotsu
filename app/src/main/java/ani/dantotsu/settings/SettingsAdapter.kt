package ani.dantotsu.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ani.dantotsu.databinding.ItemSettingsBinding
import ani.dantotsu.databinding.ItemSettingsSwitchBinding
import ani.dantotsu.setAnimation

class SettingsAdapter(private val settings: ArrayList<Settings>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class SettingsViewHolder(val binding: ItemSettingsBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class SettingsSwitchViewHolder(val binding: ItemSettingsSwitchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> SettingsViewHolder(
                ItemSettingsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            2 -> SettingsSwitchViewHolder(
                ItemSettingsSwitchBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> SettingsViewHolder(
                ItemSettingsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val settings = settings[position]
    when (settings.type) {
        1 -> {
            val b = (holder as SettingsViewHolder).binding
            setAnimation(b.root.context, b.root)

            // Set up common views
            val clickableViews = arrayOf(
                b.settingsTitle,
                b.settingsDesc,
                b.settingsIcon,
                b.settingsLayout
            )

            // Populate views
            b.settingsTitle.text = settings.name
            b.settingsDesc.text = settings.desc
            b.settingsIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    b.root.context, settings.icon
                )
            )

            // Set up click and long-click listeners for all views
            clickableViews.forEach { view ->
                view.setOnClickListener {
                    settings.onClick?.invoke(b)
                }
                view.setOnLongClickListener {
                    settings.onLongClick?.invoke()
                    true
                }
                
                // Visibility and alpha control
                view.isClickable = settings.isVisible
                view.isEnabled = settings.isVisible
                view.alpha = if (settings.isVisible) 1f else 0.5f
            }

            // Additional view setups
            b.settingsIconRight.visibility = 
                if (settings.isActivity) View.VISIBLE else View.GONE
            b.attachView.visibility = 
                if (settings.attach != null) View.VISIBLE else View.GONE
            
            settings.attach?.invoke(b)
        }

        2 -> {
            val b = (holder as SettingsSwitchViewHolder).binding
            setAnimation(b.root.context, b.root)

            // Set up common views
            val clickableViews = arrayOf(
                b.settingsDesc,
                b.settingsIcon,
                b.settingsLayout
            )

            // Populate views
            b.settingsButton.text = settings.name
            b.settingsDesc.text = settings.desc
            b.settingsIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    b.root.context, settings.icon
                )
            )

            // Switch-specific setup
            b.settingsButton.isChecked = settings.isChecked
            b.settingsButton.setOnCheckedChangeListener { _, isChecked ->
                settings.switch?.invoke(isChecked, b)
            }

            // Set up click and long-click listeners for all views
            // Combine clickable views with the button
            (clickableViews + b.settingsButton).forEach { view ->
                view.setOnClickListener {
                    // Toggle the switch when any view is clicked
                    b.settingsButton.isChecked = !b.settingsButton.isChecked
                }
                
                view.setOnLongClickListener {
                    settings.onLongClick?.invoke()
                    true
                }
                
                // Visibility and alpha control
                view.isClickable = settings.isVisible
                view.isEnabled = settings.isVisible
                view.alpha = if (settings.isVisible) 1f else 0.5f
            }

            settings.attachToSwitch?.invoke(b)
        }
    }
}

    override fun getItemCount(): Int = settings.size

    override fun getItemViewType(position: Int): Int {
        return settings[position].type
    }
}