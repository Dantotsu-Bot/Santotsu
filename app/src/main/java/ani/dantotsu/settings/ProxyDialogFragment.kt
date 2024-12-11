package ani.dantotsu.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ani.dantotsu.BottomSheetDialogFragment
import ani.dantotsu.databinding.BottomSheetProxyBinding
import ani.dantotsu.settings.saving.PrefManager
import ani.dantotsu.settings.saving.PrefName
import ani.dantotsu.restartApp

class ProxyDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetProxyBinding? = null
    private val binding get() = _binding!!

    private var proxyHost = PrefManager.getVal<String>(PrefName.Socks5ProxyHost).orEmpty()
    private var proxyPort = PrefManager.getVal<String>(PrefName.Socks5ProxyPort).orEmpty()
    private var proxyUsername = PrefManager.getVal<String>(PrefName.Socks5ProxyUsername).orEmpty()
    private var proxyPassword = PrefManager.getVal<String>(PrefName.Socks5ProxyPassword).orEmpty()
    private val authEnabled = PrefManager.getVal<Boolean>(PrefName.ProxyAuthEnabled)
    private val proxyEnabled = PrefManager.getVal<Boolean>(PrefName.EnableSocks5Proxy)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomSheetProxyBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            proxyHost.setText(this@ProxyDialogFragment.proxyHost)
            proxyPort.setText(this@ProxyDialogFragment.proxyPort)
            proxyUsername.setText(this@ProxyDialogFragment.proxyUsername)
            proxyPassword.setText(this@ProxyDialogFragment.proxyPassword)
            proxyAuthentication.isChecked = authEnabled
            toggleAuthFields(authEnabled)

            proxySave.setOnClickListener {
                updatePreferences()
                dismiss()
                if (proxyEnabled) activity?.restartApp()
            }

            proxyAuthentication.setOnCheckedChangeListener { _, isChecked ->
                PrefManager.setVal(PrefName.ProxyAuthEnabled, isChecked)
                toggleAuthFields(isChecked)
            }
        }
    }

    private fun updatePreferences() {
        with(binding) {
            proxyHost = proxyHost.text.toString()
            proxyPort = proxyPort.text.toString()
            proxyUsername = proxyUsername.text.toString()
            proxyPassword = proxyPassword.text.toString()

            PrefManager.setVal(PrefName.Socks5ProxyHost, proxyHost)
            PrefManager.setVal(PrefName.Socks5ProxyPort, proxyPort)
            PrefManager.setVal(PrefName.Socks5ProxyUsername, proxyUsername)
            PrefManager.setVal(PrefName.Socks5ProxyPassword, proxyPassword)
        }
    }

    private fun toggleAuthFields(enabled: Boolean) {
        with(binding) {
            proxyUsername.isEnabled = enabled
            proxyPassword.isEnabled = enabled
            proxyUsernameLayout.isEnabled = enabled
            proxyPasswordLayout.isEnabled = enabled
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}