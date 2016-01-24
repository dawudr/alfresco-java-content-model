<?xml version="1.0" encoding="utf-8"?>
<#if code == 400>
<response>
  <code>${code}</code>
  <message>${message}</message>
</response>
<#else>
<AiIndex>${aiIndex}</AiIndex>
</#if>
