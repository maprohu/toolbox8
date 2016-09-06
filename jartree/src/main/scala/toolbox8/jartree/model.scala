package toolbox8.jartree

import jartree.util.{CaseJarKey, RunRequestImpl}

sealed case class RunRequestAttachment(
  key: CaseJarKey,
  size: Long
)

sealed case class RunRequestWithAttachments(
  request: RunRequestImpl,
  attachments: Seq[RunRequestAttachment]
)

