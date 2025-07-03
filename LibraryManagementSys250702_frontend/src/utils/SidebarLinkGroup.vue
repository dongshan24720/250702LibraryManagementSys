<template>
  <li>
    <a
      class="flex items-center text-sm font-bold text-slate-800"
      href="#0"
      @click.prevent="open = !open"
      aria-haspopup="true"
      :aria-expanded="open"
    >
      <svg class="fill-slate-400 mr-2" :class="{ 'rotate-90': open }" width="8" height="8" xmlns="http://www.w3.org/2000/svg">
        <path d="M2.854.647A.5.5 0 0 0 2 1v6a.5.5 0 0 0 .854.354l3-3a.5.5 0 0 0 .058-.638l-.058-.07-3-3Z" />
      </svg>
      <span>{{title}}</span>
    </a>
    <ul class="pl-6 mt-2 space-y-2 text-sm" :class="{ 'hidden': !open }">
      <slot />
    </ul>
  </li>
</template>
<script>
export default {
  name: 'SidebarLinkGroup',
  props: {
    title: {
      type: String,
      default: null,
      required: true
    },
    open: {
      type: Boolean,
      default: false
    },    
  },
}
</script>